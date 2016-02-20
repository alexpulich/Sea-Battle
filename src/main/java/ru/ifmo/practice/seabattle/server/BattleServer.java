package ru.ifmo.practice.seabattle.server;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;
import ru.ifmo.practice.seabattle.battle.*;
import ru.ifmo.practice.seabattle.exceptions.FieldAlreadySetException;
import ru.ifmo.practice.seabattle.exceptions.IllegalNumberOfShipException;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpSession;
import javax.websocket.Session;
import java.io.IOException;
import java.util.*;

abstract class BattleServer extends HttpServlet implements FieldChangesListener,
        NextTurnListener, BattleEndedListener {
    protected static HashMap<String, Player> players = new HashMap<>();

    protected void onOpen(Session session, HttpSession httpSession) throws IOException {
        Log.getInstance().sendMessage(this.getClass(), session.getId(), "Соединение установлено");

        Object nickName = httpSession.getAttribute("nickName");
        if (nickName != null) players.put(session.getId(), new Player(session, nickName.toString()));
        else players.put(session.getId(), new Player(session, "Игрок " + httpSession.getId()));
    }

    protected void onClose(Session session) throws IOException {
        Log.getInstance().sendMessage(this.getClass(), session.getId(), "Соединение разорвано");

        players.remove(session.getId());
    }

    protected void onMessage(String message, Session session) throws IOException {
        Log.getInstance().sendMessage(this.getClass(), session.getId(), "Получено сообщение: " + message);

        try {
            players.get(session.getId()).setLastCommand(parseMessage(message, session));
        } catch (FieldAlreadySetException e) {
            sendMessage(new Message<>(Notice.Error), session);
        }
    }

    private Command parseMessage(String message, Session session) throws IOException {
        Command result = null;

        Command lastCommand = players.get(session.getId()).popLastCommand();

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

                    case AddShip:
                        result = command;
                        sendMessage(new Message<>(Notice.ExpectedAddShip), session);
                        break;

                    case RemoveShip:
                        result = command;
                        sendMessage(new Message<>(Notice.ExpectedRemoveShip), session);
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

                case AddShip:
                    addShip(message, session);
                    break;

                case RemoveShip:
                    removeShip(message, session);
                    break;

                default:
                    sendMessage(new Message<>(Notice.Error), session);
                    break;
            }
        }

        return result;
    }

    protected void sendMessage(Message message, Session session) throws IOException {
        GsonBuilder gsonBuilder = new GsonBuilder();
        Gson gson = gsonBuilder.serializeNulls().create();

        Log.getInstance().sendMessage(this.getClass(), session.getId(),
                "Отправлено сообщение: " + gson.toJson(message));

        session.getBasicRemote().sendText(gson.toJson(message));
    }

    protected void setFirstField(Player player) throws FieldAlreadySetException {
        if (!player.isInBattle()) {
            if (player.getFirstField() != null)
                player.getFirstField().removeChangesListener(this);
            player.createFirstField();
            player.getFirstField().addChangesListener(this);
        } else throw new FieldAlreadySetException();
    }

    private void placeShipsRandom(Session session) throws IOException {
        FirstFieldBuilder builder = new FirstFieldBuilder();
        builder.placeShipsRandom();

        try {
            players.get(session.getId()).setFirstFieldBuilder(builder);
        } catch (FieldAlreadySetException e) {
            sendMessage(new Message<>(Notice.Error), session);
            return;
        }

        sendMessage(new Message<>(builder.create().getCurrentShips()), session);
    }

    private void setField(String message, Session session) throws IOException {
        Player player = players.get(session.getId());

        Cell[][] fieldCells;

        try {
            fieldCells = new Gson().fromJson(message, Cell[][].class);
        } catch (JsonSyntaxException | JsonIOException e) {
            sendMessage(new Message<>(Notice.Error), session);
            return;
        }

        FirstFieldBuilder firstFieldBuilder = new FirstFieldBuilder();
        FirstField firstField;

        try {
            firstFieldBuilder.addShips(fieldCells);
            player.setFirstFieldBuilder(firstFieldBuilder);
        } catch (IllegalNumberOfShipException | IllegalArgumentException
                | FieldAlreadySetException e) {
            sendMessage(new Message<>(Notice.Error), session);
            return;
        }

        sendMessage(new Message<>(Notice.FieldSet), session);
    }

    private void addShip(String message, Session session) throws IOException {
        Player player = players.get(session.getId());

        FirstFieldBuilder builder = player.getFirstFieldBuilder();
        try {
            if (builder == null) {
                builder = new FirstFieldBuilder();
                player.setFirstFieldBuilder(builder);
            }

            Coordinates[] coordinates = new Gson().fromJson(message, Coordinates[].class);
            HashSet<Coordinates> ship = new HashSet<>();
            ship.addAll(Arrays.asList(coordinates));

            builder.addShip(ship);

            sendMessage(new Message<>(Notice.ShipAdded), session);
        } catch (IllegalNumberOfShipException | IllegalArgumentException
                | FieldAlreadySetException | JsonSyntaxException
                | JsonIOException e) {
            sendMessage(new Message<>(Notice.Error), session);
        }
    }

    private void removeShip(String message, Session session) throws IOException {
        Player player = players.get(session.getId());

        FirstFieldBuilder builder = player.getFirstFieldBuilder();
        try {
            if (builder == null) {
                builder = new FirstFieldBuilder();
                player.setFirstFieldBuilder(builder);
            }

            Coordinates[] coordinates = new Gson().fromJson(message, Coordinates[].class);
            HashSet<Coordinates> ship = new HashSet<>();
            ship.addAll(Arrays.asList(coordinates));

            if (!builder.removeShip(ship))
                sendMessage(new Message<>(Notice.Error), session);
            else {
                sendMessage(new Message<>(Notice.ShipRemoved), session);
            }
        } catch (FieldAlreadySetException | JsonSyntaxException
                | JsonIOException e) {
            sendMessage(new Message<>(Notice.Error), session);
        }
    }

    private void shot(String message, Session session) throws IOException {
        Player player = players.get(session.getId());

        if (player.popTurn()) {
            try {
                Coordinates shot = new Gson().fromJson(message, Coordinates.class);
                player.setShot(shot);
            } catch (JsonSyntaxException | JsonIOException | IllegalArgumentException e) {
                sendMessage(new Message<>(Notice.Error), session);
                player.yourTurn();
            }
        } else sendMessage(new Message<>(Notice.Error), session);
    }

/*    protected String getPlayerSessionId(HashMap<String, Player> map, Player value) {
        Set<Map.Entry<String, Player>> entrySet = map.entrySet();
        for (Map.Entry<String, Player> entry : entrySet) {
            if (entry.getValue() == value) {
                return entry.getKey();
            }
        }

        return null;
    }*/

    abstract protected void startBattle(Session session) throws IOException;

    @Override
    abstract public void battleEnd(Gamer winner, Gamer loser);

    @Override
    public void nextTurn(Gamer gamer) {
        if (gamer instanceof Player) {
            Player player = (Player) gamer;
            player.yourTurn();

            try {
                sendMessage(new Message<>(Notice.YourTurn), player.getSession());
            } catch (IOException e) {
                System.err.print(e.getMessage());
            }
        }
    }

    @Override
    public void fieldChanged(Field field, Coordinates hit, HashSet<Coordinates> misses) {
        Set<Map.Entry<String, Player>> entrySet = players.entrySet();

        for (Map.Entry<String, Player> entry : entrySet) {
            if (entry.getValue().getFirstField() == field) {
                try {
                    sendMessage(new Message<>(new FieldChanges(FieldStatus.First, hit, misses)),
                            entry.getValue().getSession());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else if (entry.getValue().getSecondField() == field) {
                try {
                    sendMessage(new Message<>(new FieldChanges(FieldStatus.Second, hit, misses)),
                            entry.getValue().getSession());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
