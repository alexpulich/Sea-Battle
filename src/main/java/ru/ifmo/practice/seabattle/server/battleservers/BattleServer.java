package ru.ifmo.practice.seabattle.server.battleservers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;
import ru.ifmo.practice.seabattle.battle.*;
import ru.ifmo.practice.seabattle.exceptions.BattleAlreadyStartException;
import ru.ifmo.practice.seabattle.exceptions.CommandAlreadySetException;
import ru.ifmo.practice.seabattle.exceptions.IllegalNumberOfShipException;
import ru.ifmo.practice.seabattle.server.*;
import ru.ifmo.practice.seabattle.server.Error;

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
        } catch (CommandAlreadySetException e) {
            sendMessage(new Message<>(Error.CommandAlreadySet), session);
        }
    }

    private Command parseMessage(String message, Session session) throws IOException {
        Command result = null;
        Command lastCommand = players.get(session.getId()).popLastCommand();
        Player player = players.get(session.getId());

        try {
            if (lastCommand == null) {
                Command command = null;

                try {
                    command = new Gson().fromJson(message, Command.class);
                } catch (JsonSyntaxException e) {
                    sendMessage(new Message<>(Error.IncorrectJsonSyntax), session);
                }

                if (command != null) {
                    switch (command) {
                        case PlaceShipsRandom:
                            sendMessage(new Message<>(placeShipsRandom(player)), session);
                            break;

                        case StartBattle:
                            try {
                                startBattle(player);
                            } catch (IllegalArgumentException | IllegalNumberOfShipException e) {
                                sendMessage(new Message<>(Error.IncorrectField), session);
                            }
                            break;

                        case SetField:
                            result = command;
                            sendMessage(new Message<>(Notice.ExpectedField), session);
                            break;

                        case Shot:
                            if (players.get(session.getId()).isInBattle()) {
                                result = command;
                                sendMessage(new Message<>(Notice.ExpectedCoordinates), session);
                            } else sendMessage(new Message<>(Error.BattleNotStart), session);
                            break;

                        case AddShip:
                            result = command;
                            sendMessage(new Message<>(Notice.ExpectedAddShip), session);
                            break;

                        case RemoveShip:
                            result = command;
                            sendMessage(new Message<>(Notice.ExpectedRemoveShip), session);
                            break;

                        case MoveShip:
                            result = command;
                            sendMessage(new Message<>(Notice.ExpectedShipMovement), session);
                            break;

                        default:
                            sendMessage(new Message<>(Error.IncorrectCommand), session);
                            break;
                    }
                } else {
                    sendMessage(new Message<>(Error.IncorrectCommand), session);
                }
            } else {
                try {
                    switch (lastCommand) {
                        case SetField:
                            Cell[][] field = new Gson().fromJson(message, Cell[][].class);

                            try {
                                if (field != null) setField(field, player);
                            } catch (IllegalNumberOfShipException | IllegalArgumentException e) {
                                sendMessage(new Message<>(Error.IncorrectField), session);
                            }

                            sendMessage(new Message<>(Notice.FieldSet), session);
                            break;

                        case Shot:
                            Coordinates shot = new Gson().fromJson(message, Coordinates.class);

                            try {
                                if (shot != null)
                                    if (!shot(shot, player))
                                        sendMessage(new Message<>(Error.NotYourTurn), session);
                            } catch (IllegalArgumentException e) {
                                sendMessage(new Message<>(Error.ShotRepeated), session);
                                player.yourTurn();
                            }

                            break;

                        case AddShip:
                            HashSet<Coordinates> shipToAdd = new HashSet<>();
                            shipToAdd.addAll(Arrays.asList(new Gson().fromJson(message, Coordinates[].class)));

                            try {
                                if (!shipToAdd.isEmpty()) {
                                    addShip(shipToAdd, player);
                                    sendMessage(new Message<>(Notice.ShipAdded), session);
                                } else sendMessage(new Message<>(Error.IncorrectShip), session);
                            } catch (IllegalNumberOfShipException e) {
                                sendMessage(new Message<>(Error.FieldFull), session);
                            } catch (IllegalArgumentException e) {
                                sendMessage(new Message<>(Error.IncorrectShip), session);
                            }

                            break;

                        case RemoveShip:
                            HashSet<Coordinates> shipToRemove = new HashSet<>();
                            shipToRemove.addAll(Arrays.asList(new Gson().fromJson(message, Coordinates[].class)));

                            if (!shipToRemove.isEmpty()) {
                                if (removeShip(shipToRemove, player))
                                    sendMessage(new Message<>(Notice.ShipRemoved), session);
                                else sendMessage(new Message<>(Error.ShipNotFound), session);
                            } else sendMessage(new Message<>(Error.IncorrectShip), session);

                            break;

                        case MoveShip:
                            ShipMovement movement = new Gson().fromJson(message, ShipMovement.class);

                            if (movement != null) {
                                if (moveShip(movement, player))
                                    sendMessage(new Message<>(movement.getNewPlace()), session);
                                else sendMessage(new Message<>(movement.getOldPlace()), session);
                            }

                            break;

                        default:
                            sendMessage(new Message<>(Error.IncorrectCommand), session);
                            break;
                    }
                } catch (JsonSyntaxException | JsonIOException e) {
                    try {
                        player.setLastCommand(lastCommand);
                    } catch (CommandAlreadySetException e1) {
                        e1.printStackTrace();
                    }
                    sendMessage(new Message<>(Error.IncorrectJsonSyntax), session);
                }
            }
        } catch (BattleAlreadyStartException e) {
            sendMessage(new Message<>(Error.BattleAlreadyStart), session);
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

    private ArrayList<HashSet<Coordinates>> placeShipsRandom(Player player)
            throws IOException, BattleAlreadyStartException {
        FirstFieldBuilder builder = new FirstFieldBuilder();
        builder.placeShipsRandom();
        player.setFirstFieldBuilder(builder);
        return builder.create().getCurrentShips();
    }

    private void setField(Cell[][] cells, Player player) throws IOException, BattleAlreadyStartException {
        FirstFieldBuilder firstFieldBuilder = new FirstFieldBuilder();

        firstFieldBuilder.addShips(cells);
        player.setFirstFieldBuilder(firstFieldBuilder);
    }

    private void addShip(HashSet<Coordinates> ship, Player player) throws IOException, BattleAlreadyStartException {
        FirstFieldBuilder builder = player.getFirstFieldBuilder();

        if (builder == null) {
            builder = new FirstFieldBuilder();
            player.setFirstFieldBuilder(builder);
        }

        builder.addShip(ship);
    }

    private boolean removeShip(HashSet<Coordinates> ship, Player player)
            throws IOException, BattleAlreadyStartException {
        FirstFieldBuilder builder = player.getFirstFieldBuilder();

        if (builder == null) {
            builder = new FirstFieldBuilder();
            player.setFirstFieldBuilder(builder);
        }

        return builder.removeShip(ship);
    }

    private boolean moveShip(ShipMovement movement, Player player) throws IOException, BattleAlreadyStartException {
        if (removeShip(movement.getOldPlace(), player)) {
            try {
                addShip(movement.getNewPlace(), player);
                return true;
            } catch (IllegalArgumentException | IllegalNumberOfShipException e) {
                addShip(movement.getOldPlace(), player);
                return false;
            }
        } else return false;
    }

    private boolean shot(Coordinates shot, Player player) throws IOException {
        if (player.popTurn()) {
            player.setShot(shot);
            return true;
        } else return false;
    }

    abstract protected void startBattle(Player player) throws IOException, BattleAlreadyStartException;

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

                    return;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else if (entry.getValue().getSecondField() == field) {
                try {
                    sendMessage(new Message<>(new FieldChanges(FieldStatus.Second, hit, misses)),
                            entry.getValue().getSession());

                    return;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
