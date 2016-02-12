package ru.ifmo.practice.seabattle.server;

import com.google.gson.Gson;
import ru.ifmo.practice.seabattle.battle.*;
import ru.ifmo.practice.seabattle.exceptions.IllegalNumberOfShipException;

import javax.servlet.http.HttpServlet;
import javax.websocket.Session;
import java.io.IOException;
import java.util.HashSet;

class Server extends HttpServlet {
    protected Command parseMessage(Command lastCommand, String message,
                                  SeaBattleServer server, String sessionID) throws IOException {
        Command result = null;

        if (lastCommand == null) {
            Command command = new Gson().fromJson(message, Command.class);
            if (command != null) {
                switch (command) {
                    case PlaceShipsRandom:
                        server.placeShipsRandom(sessionID);
                        break;

                    case StartBattle:
                        server.startBattle(sessionID);
                        break;

                    default:
                        result = command;
                        break;
                }
            }
        } else {
            switch (lastCommand) {
                case SetField:
                    server.setField(message, sessionID);
                    break;

                case Shot:
                    server.shot(message, sessionID);
                    break;
            }
        }

        return result;
    }

    protected void sendMessage(String message, Session session) throws IOException {
        session.getBasicRemote().sendText(message);
    }

    protected Field placeShipsRandom(ShotListener listener) {
        FieldBuilder builder = new FieldBuilder();
        builder.placeShipsRandom();
        Field field = builder.create();
        field.addShotListener(listener);
        return field;
    }

    protected Field setField(String message, ShotListener listener) throws IOException {
        Cell[][] fieldCells = new Gson().fromJson(message, Cell[][].class);

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

    protected FieldChanges shot(String message, Player player) {
        Coordinates shot = new Gson().fromJson(message, Coordinates.class);
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
}
