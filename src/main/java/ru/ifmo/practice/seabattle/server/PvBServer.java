package ru.ifmo.practice.seabattle.server;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import ru.ifmo.practice.seabattle.battle.*;
import ru.ifmo.practice.seabattle.battle.bot.Bot;
import ru.ifmo.practice.seabattle.exceptions.IllegalNumberOfShipException;

import javax.servlet.http.HttpServlet;
import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@ServerEndpoint("/pvbserver")
public class PvBServer extends HttpServlet implements ShotListener {
    private static HashMap<String, Session> sessions = new HashMap<>();
    private static HashMap<String, Player> players = new HashMap<>();
    private static HashMap<String, Battle> battles = new HashMap<>();
    private static HashMap<String, Command> commands = new HashMap<>();
    private static HashMap<String, Field> fields = new HashMap<>();

    @OnOpen
    public void onOpen(Session session) throws IOException {
        sessions.put(session.getId(), session);
        commands.put(session.getId(), null);
    }

    @OnClose
    public void onClose(Session session) throws IOException {
        sessions.remove(session.getId());
        if (players.containsKey(session.getId())) players.remove(session.getId());
        if (battles.containsKey(session.getId())) battles.remove(session.getId());
        if (fields.containsKey(session.getId())) {
            Field field = fields.get(session.getId());
            field.removeShotListener(this);
            fields.remove(session.getId());
        }
        commands.remove(session.getId());
    }

    @OnMessage
    public void parseMessage(String message, Session session) throws IOException {
        Command lastCommand = commands.get(session.getId());
        if (lastCommand == null) {
            Command command = parseCommand(message);
            if (command != null) {
                switch (command) {
                    case PlaceShipsRandom:
                        placeShipsRandom(session.getId());
                        break;

                    case StartBattle:
                        startBattle(session.getId());
                        break;

                    default:
                        commands.put(session.getId(), command);
                        break;
                }
            }
        } else {
            switch (lastCommand) {
                case SetField:
                    setField(message, session.getId());
                    break;

                case Shot:
                    shot(message, session.getId());
            }
        }
    }

    private void sendMessage(String message, Session session) throws IOException {
        session.getBasicRemote().sendText(message);
    }

    private void sendMessage(String message, String sessionId) throws IOException {
        sendMessage(message, sessions.get(sessionId));
    }

    private Command parseCommand(String message) {
        return new Gson().fromJson(message, Command.class);
    }

    private void placeShipsRandom(String sessionID) {
        FieldBuilder builder = new FieldBuilder();
        builder.placeShipsRandom();
        try {
            Field field = builder.create();
            field.addShotListener(this);
            fields.put(sessionID, field);
        } catch (IllegalNumberOfShipException e) {
            e.printStackTrace();
        }
    }

    private void setField(String message, String sessionId) throws IOException {
        Cell[][] fieldCells = new Gson().fromJson(message, Cell[][].class);

        FieldBuilder fieldBuilder = new FieldBuilder();
        Field field;

        GsonBuilder gsonBuilder = new GsonBuilder();
        Gson gson = gsonBuilder.create();

        try {
            fieldBuilder.addShips(fieldCells);
            field = fieldBuilder.create();
            field.addShotListener(this);
        } catch (IllegalNumberOfShipException | IllegalArgumentException e) {
            sendMessage(gson.toJson(e.getMessage()), sessionId);
            return;
        }

        fields.put(sessionId, field);
        sendMessage(gson.toJson("ok"), sessionId);
    }

    private void startBattle(String sessionId) {
        Player player;

        if (fields.containsKey(sessionId)) {
            player = new Player("Игрок", fields.get(sessionId));
            players.put(sessionId, player);
        } else return;

        Battle battle = new Battle(player, new Bot("Бот"));
        battles.put(sessionId, battle);
        try {
            battle.start();
        } catch (IllegalNumberOfShipException e) {
            e.printStackTrace();
        }
    }

    private void shot(String message, String sessionId) throws IOException {
        Coordinates shot = new Gson().fromJson(message, Coordinates.class);
        Player player;

        if (players.containsKey(sessionId)) {
            player = players.get(sessionId);
        } else return;

        player.setShot(shot);

        while (player.getShotResult() == null) {
            try {
                wait(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        HashSet<Coordinates> shotResult = new HashSet<>();
        shotResult.addAll(player.getShotResult());
        player.setShotResult(null);

        Coordinates hit;
        Coordinates[] misses;

        if (shotResult.isEmpty()) {
            hit = null;
            misses = new Coordinates[1];
            misses[0] = shot;
        } else {
            hit = shot;
            shotResult.remove(shot);
            if (shotResult.isEmpty()) {
                misses = null;
            } else {
                misses = shotResult.toArray(new Coordinates[shotResult.size()]);
            }
        }

        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();
        sendMessage(gson.toJson(new FieldChanges(FieldStatus.Second, hit, misses)), sessionId);
    }

    @Override
    public void shot(Field field, Coordinates hit, HashSet<Coordinates> misses) throws IOException {
        if (fields.containsValue(field)) {
            String sessionId = "";
            Set<Map.Entry<String, Field>> entrySet = fields.entrySet();
            for (Map.Entry<String, Field> entry : entrySet) {
                if (entry.getValue().equals(field)) {
                    sessionId = entry.getKey();
                    break;
                }
            }

            FieldChanges fieldChanges = new FieldChanges(FieldStatus.First, hit,
                    misses.toArray(new Coordinates[misses.size()]));

            GsonBuilder builder = new GsonBuilder();
            Gson gson = builder.create();

            sendMessage(gson.toJson(fieldChanges), sessionId);
        }
    }
}
