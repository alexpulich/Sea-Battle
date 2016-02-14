package ru.ifmo.practice.seabattle.server;

import com.google.gson.Gson;
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

class Server extends HttpServlet {
    protected Command parseMessage(Command lastCommand, String message,
                                   BattleServer server, String sessionID) throws IOException {
        Command result = null;

        if (lastCommand == null) {
            Command command = null;

            try {
                command = new Gson().fromJson(message, Command.class);
            } catch (JsonSyntaxException e) {
                server.sendMessage(new Gson().toJson(Notice.Error), sessionID);
            }

            if (command != null) {
                switch (command) {
                    case PlaceShipsRandom:
                        server.placeShipsRandom(sessionID);
                        break;

                    case StartBattle:
                        server.startBattle(sessionID);
                        break;

                    case SetField:
                    case Shot:
                        result = command;
                        server.sendMessage(new Gson().toJson(Notice.OK), sessionID);
                        break;

                    default:
                        server.sendMessage(new Gson().toJson(Notice.Error), sessionID);
                        break;
                }
            } else {
                server.sendMessage(new Gson().toJson(Notice.Error), sessionID);
            }
        } else {
            switch (lastCommand) {
                case SetField:
                    server.setField(message, sessionID);
                    break;

                case Shot:
                    server.shot(message, sessionID);
                    break;

                default:
                    server.sendMessage(new Gson().toJson(Notice.Error), sessionID);
                    break;
            }
        }

        return result;
    }

    protected void sendMessage(String message, Session session) throws IOException {
        session.getBasicRemote().sendText(message);
    }

    protected Field placeShipsRandom(ShotInFieldListener listener) {
        FieldBuilder builder = new FieldBuilder();
        builder.placeShipsRandom();
        Field field = builder.create();
        field.addShotListener(listener);
        return field;
    }

    protected Field setField(Cell[][] fieldCells, ShotInFieldListener listener) throws IOException {
        FieldBuilder fieldBuilder = new FieldBuilder();
        Field field;

        try {
            fieldBuilder.addShips(fieldCells);
            field = fieldBuilder.create();
            field.addShotListener(listener);
        } catch (IllegalNumberOfShipException | IllegalArgumentException e) {
            return null;
        }

        return field;
    }

    protected FieldChanges shot(Coordinates shot, Player player) {
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

        return new FieldChanges(FieldStatus.Second, hit, misses);
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
}
