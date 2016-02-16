package ru.ifmo.practice.seabattle.server;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;
import ru.ifmo.practice.seabattle.battle.*;
import ru.ifmo.practice.seabattle.exceptions.IllegalNumberOfShipException;

import javax.servlet.http.HttpServlet;
import javax.websocket.Session;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

abstract class BattleServer extends HttpServlet implements FieldChangesListener, NextTurnListener, BattleEndedListener {
    protected static HashMap<String, Session> sessions = new HashMap<>();
    protected static HashMap<String, Player> players = new HashMap<>();
    protected static HashMap<String, Battle> battles = new HashMap<>();
    protected static HashMap<String, Command> commands = new HashMap<>();
    protected static HashMap<String, FirstField> firstFields = new HashMap<>();
    protected static HashMap<String, SecondField> secondFields = new HashMap<>();
    protected static HashMap<String, Boolean> turns = new HashMap<>();
    protected static HashMap<String, Thread> threads = new HashMap<>();

    protected void parseMessage(String message, Session session) throws IOException {
        Log.getInstance().sendMessage(this.getClass(), session.getId(), "Получено сообщение: " + message);

        Command result = null;
        Command lastCommand = commands.get(session.getId());

        if (lastCommand == null) {
            Command command = null;

            try {
                command = new Gson().fromJson(message, Command.class);
            } catch (JsonSyntaxException e) {
                sendMessage(new Message<>(Notice.Error), session);
            }

            if (command != null) {
                switch (command) {
                    case PlaceShipsRandom:
                        placeShipsRandom(session);
                        break;

                    case StartBattle:
                        startBattle(session);
                        break;

                    case SetField:
                        result = command;
                        sendMessage(new Message<>(Notice.ExpectedField), session);
                        break;

                    case Shot:
                        result = command;
                        sendMessage(new Message<>(Notice.ExpectedCoordinates), session);
                        break;

                    default:
                        sendMessage(new Message<>(Notice.Error), session);
                        break;
                }
            } else {
                sendMessage(new Message<>(Notice.Error), session);
            }
        } else {
            switch (lastCommand) {
                case SetField:
                    setField(message, session);
                    break;

                case Shot:
                    shot(message, session);
                    break;

                default:
                    sendMessage(new Message<>(Notice.Error), session);
                    break;
            }
        }

        commands.put(session.getId(), result);
    }

    protected void sendMessage(Message message, Session session) throws IOException {
        GsonBuilder gsonBuilder = new GsonBuilder();
        Gson gson = gsonBuilder.serializeNulls().create();

        Log.getInstance().sendMessage(this.getClass(), session.getId(),
                "Отправлено сообщение: " + gson.toJson(message));
        session.getBasicRemote().sendText(gson.toJson(message));
    }

    protected void placeShipsRandom(Session session) throws IOException {
        if (!battles.containsKey(session.getId())) {
            if (firstFields.containsKey(session.getId()))
                firstFields.get(session.getId()).removeChangesListener(this);

            FirstFieldBuilder builder = new FirstFieldBuilder();
            builder.placeShipsRandom();

            FirstField field = builder.create();
            field.addChangesListener(this);

            firstFields.put(session.getId(), field);

            sendMessage(new Message<>(field.getCurrentConditions()), session);
        } else sendMessage(new Message<>(Notice.Error), session);
    }

    abstract protected void startBattle(Session session) throws IOException;

    protected void setField(String message, Session session) throws IOException {
        commands.remove(session.getId());

        if (!battles.containsKey(session.getId())) {
            if (firstFields.containsKey(session.getId()))
                firstFields.get(session.getId()).removeChangesListener(this);

            Cell[][] fieldCells;

            try {
                fieldCells = new Gson().fromJson(message, Cell[][].class);
            } catch (JsonSyntaxException | JsonIOException e) {
                sendMessage(new Message<>(Notice.Error), session);
                return;
            }

            FirstFieldBuilder firstFieldBuilder = new FirstFieldBuilder();
            FirstField field;

            try {
                firstFieldBuilder.addShips(fieldCells);
                field = firstFieldBuilder.create();
                field.addChangesListener(this);
            } catch (IllegalNumberOfShipException | IllegalArgumentException e) {
                field = null;
            }

            if (field == null) sendMessage(new Message<>(Notice.Error), session);
            else {
                firstFields.put(session.getId(), field);
                sendMessage(new Message<>(Notice.OK), session);
            }
        } else sendMessage(new Message<>(Notice.Error), session);
    }

    protected void shot(String message, Session session) throws IOException {
        commands.remove(session.getId());
        if (players.containsKey(session.getId()) && turns.containsKey(session.getId())
                && turns.get(session.getId())) {
            Coordinates shot;

            try {
                shot = new Gson().fromJson(message, Coordinates.class);
                Player player = players.get(session.getId());
                player.setShot(shot);
                turns.put(session.getId(), false);
            } catch (JsonSyntaxException | JsonIOException | IllegalArgumentException e) {
                sendMessage(new Message<>(Notice.Error), session);
            }
            
        } else sendMessage(new Message<>(Notice.Error), session);
    }

    protected <V> String getSessionId(HashMap<String, V> map, V value) {
        Set<Map.Entry<String, V>> entrySet = map.entrySet();
        for (Map.Entry<String, V> entry : entrySet) {
            if (entry.getValue() == value) {
                return entry.getKey();
            }
        }

        return null;
    }

    @Override
    abstract public void battleEnd(Gamer winner, Gamer loser);

    @Override
    public void nextTurn(Gamer gamer) {
        if (players.containsValue(gamer)) {
            String sessionId = getSessionId(players, (Player)gamer);

            turns.put(sessionId, true);

            try {
                sendMessage(new Message<>(Notice.YourTurn), sessions.get(sessionId));
            } catch (IOException e) {
                System.err.print(e.getMessage());
            }
        }
    }

    @Override
    public void fieldChanged(Field field, Coordinates hit, HashSet<Coordinates> misses) {
        if (field instanceof FirstField && firstFields.containsValue(field)) {
            String sessionId = getSessionId(firstFields, (FirstField)field);

            try {
                FieldChanges fieldChanges = new FieldChanges(FieldStatus.First, hit, misses);
                Message<FieldChanges> message = new Message<>(fieldChanges);
                Session session = sessions.get(sessionId);

                sendMessage(message, session);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if (field instanceof SecondField && secondFields.containsValue(field)) {
            String sessionId = getSessionId(secondFields, (SecondField) field);

            try {
                sendMessage(new Message<>(new FieldChanges(FieldStatus.Second, hit,
                        misses)), sessions.get(sessionId));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
