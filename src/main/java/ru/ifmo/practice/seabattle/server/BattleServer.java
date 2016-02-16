package ru.ifmo.practice.seabattle.server;

import com.google.gson.Gson;
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

abstract class BattleServer extends HttpServlet implements ShotInFieldListener, NextTurnListener, BattleEndedListener {
    protected static HashMap<String, Session> sessions = new HashMap<>();
    protected static HashMap<String, Player> players = new HashMap<>();
    protected static HashMap<String, Battle> battles = new HashMap<>();
    protected static HashMap<String, Command> commands = new HashMap<>();
    protected static HashMap<String, Field> fields = new HashMap<>();
    protected static HashMap<String, Boolean> turns = new HashMap<>();
    protected static HashMap<String, Thread> threads = new HashMap<>();

    protected void parseMessage(String message, Session session) throws IOException {
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
                    case Shot:
                        result = command;
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
        session.getBasicRemote().sendText(new Gson().toJson(message));
    }

    protected void placeShipsRandom(Session session) throws IOException {
        if (!battles.containsKey(session.getId())) {
            FieldBuilder builder = new FieldBuilder();
            builder.placeShipsRandom();
            Field field = builder.create();
            field.addShotListener(this);
            fields.put(session.getId(), field);
            sendMessage(new Message<>(field.getCurrentConditions()), session);
        } else sendMessage(new Message<>(Notice.Error), session);
    }

    abstract protected void startBattle(Session session) throws IOException;

    protected void setField(String message, Session session) throws IOException {
        commands.remove(session.getId());

        if (!battles.containsKey(session.getId())) {
            Cell[][] fieldCells;

            try {
                fieldCells = new Gson().fromJson(message, Cell[][].class);
            } catch (JsonSyntaxException | JsonIOException e) {
                sendMessage(new Message<>(Notice.Error), session);
                return;
            }

            FieldBuilder fieldBuilder = new FieldBuilder();
            Field field;

            try {
                fieldBuilder.addShips(fieldCells);
                field = fieldBuilder.create();
                field.addShotListener(this);
            } catch (IllegalNumberOfShipException | IllegalArgumentException e) {
                field = null;
            }

            if (field == null) sendMessage(new Message<>(Notice.Error), session);
            else {
                fields.put(session.getId(), field);
                sendMessage(new Message<>(Notice.OK), session);
            }
        } else sendMessage(new Message<>(Notice.Error), session);
    }

    protected void shot(String message, Session session) throws IOException {
        commands.remove(session.getId());
        if (players.containsKey(session.getId()) && turns.containsKey(session.getId())
                && turns.get(session.getId())) {
            turns.put(session.getId(), false);
            Coordinates shot;

            try {
                shot = new Gson().fromJson(message, Coordinates.class);
                Player player = players.get(session.getId());

                player.setShot(shot);
                HashSet<Coordinates> shotResult = new HashSet<>();
                shotResult.addAll(player.getShotResult());
                player.setShotResult(null);

                Coordinates hit;
                HashSet<Coordinates> misses = new HashSet<>();

                if (shotResult.isEmpty()) {
                    hit = null;
                    misses.add(shot);
                } else {
                    hit = shot;
                    shotResult.remove(shot);
                    if (shotResult.isEmpty()) {
                        misses = null;
                    } else {
                        misses = shotResult;
                    }
                }

                sendMessage(new Message<>(new FieldChanges(FieldStatus.Second, hit, misses)), session);
            } catch (JsonSyntaxException | JsonIOException | IllegalArgumentException e) {
                sendMessage(new Message<>(Notice.Error), session);
            }

        } else sendMessage(new Message<>(Notice.Error), session);
    }

    protected <V> String getSessionId(HashMap<String, V> map, V value) {
        Set<Map.Entry<String, V>> entrySet = map.entrySet();
        for (Map.Entry<String, V> entry : entrySet) {
            if (entry.getValue().equals(value)) {
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
    public void shotInField(Field field, Coordinates hit, HashSet<Coordinates> misses) {
        if (fields.containsValue(field)) {
            String sessionId = getSessionId(fields, field);

            try {
                sendMessage(new Message<>(new FieldChanges(FieldStatus.First, hit,
                        misses)), sessions.get(sessionId));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
