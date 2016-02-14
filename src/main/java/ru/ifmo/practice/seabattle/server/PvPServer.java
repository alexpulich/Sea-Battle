package ru.ifmo.practice.seabattle.server;

import com.google.gson.Gson;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;
import ru.ifmo.practice.seabattle.battle.*;

import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.*;

@ServerEndpoint("/pvpserver")
public class PvPServer extends Server implements BattleServer {
    private static ArrayList<Room> freeRooms = new ArrayList<>();
    private static ArrayList<Room> fullRooms = new ArrayList<>();
    private static HashMap<String, Session> sessions = new HashMap<>();
    private static HashMap<String, Player> players = new HashMap<>();
    private static HashMap<String, Battle> battles = new HashMap<>();
    private static HashMap<String, Command> commands = new HashMap<>();
    private static HashMap<String, Field> fields = new HashMap<>();
    private static HashMap<String, Boolean> readyToBattle = new HashMap<>();
    private static HashMap<String, Boolean> turns = new HashMap<>();
    private static HashMap<String, Thread> threads = new HashMap<>();

    @OnOpen
    public void onOpen(Session session) throws IOException {
        sessions.put(session.getId(), session);
        commands.put(session.getId(), null);
        readyToBattle.put(session.getId(), false);
        if (freeRooms.isEmpty()) {
            Room room = new Room();
            room.add(session.getId());
            freeRooms.add(room);
        } else {
            Room room = freeRooms.get(0);
            freeRooms.remove(room);
            room.add(session.getId());
            fullRooms.add(room);

            String message = new Gson().toJson(Notice.OpponentFound);
            sendMessage(message, room.getPlayer1());
            sendMessage(message, room.getPlayer2());
        }
    }

    @OnClose
    public void onClose(Session session) throws IOException {
        onClose(session.getId());

        boolean isContains = false;
        Iterator<Room> iterator = freeRooms.iterator();

        while (!isContains && iterator.hasNext()) {
            if (iterator.next().contains(session.getId())) {
                iterator.remove();
                isContains = true;
            }
        }

        iterator = fullRooms.iterator();
        while (!isContains && iterator.hasNext()) {
            Room room = iterator.next();
            if (room.contains(session.getId())) {
                String opponentId = session.getId().equals(room.getPlayer1()) ?
                        room.getPlayer2() : room.getPlayer1();

                Session opponent = sessions.get(opponentId);
                onClose(opponentId);
                opponent.close();

                iterator.remove();
                isContains = true;
            }
        }
    }

    private void onClose(String sessionId) {
        sessions.remove(sessionId);
        if (players.containsKey(sessionId)) {
            players.remove(sessionId);
        }
        if (battles.containsKey(sessionId)) {
            Battle battle = battles.get(sessionId);
            battle.removeBattleEndedListener(this);
            battles.remove(sessionId);
        }
        if (fields.containsKey(sessionId)) {
            Field field = fields.get(sessionId);
            field.removeShotListener(this);
            fields.remove(sessionId);
        }
        if (turns.containsKey(sessionId)) {
            turns.remove(sessionId);
        }
        if (threads.containsKey(sessionId)) {
            Thread thread = threads.get(sessionId);
            thread.interrupt();
            threads.remove(sessionId);
        }
        commands.remove(sessionId);
        if (readyToBattle.containsKey(sessionId))
            readyToBattle.remove(sessionId);
    }

    @OnMessage
    public void onMessage(String message, Session session) throws IOException {
        commands.put(session.getId(),
                parseMessage(commands.get(session.getId()), message, this, session.getId()));
    }

    @Override
    public void sendMessage(String message, String sessionId) throws IOException {
        sendMessage(message, sessions.get(sessionId));
    }

    @Override
    public void placeShipsRandom(String sessionID) throws IOException {
        Field field = placeShipsRandom(this);
        fields.put(sessionID, field);
        sendMessage(new Gson().toJson(field.getCurrentConditions()), sessionID);
    }

    @Override
    public void setField(String message, String sessionId) throws IOException {
        Cell[][] fieldCells;

        try {
            fieldCells = new Gson().fromJson(message, Cell[][].class);
        } catch (JsonSyntaxException | JsonIOException e) {
            sendMessage(new Gson().toJson(Notice.Error), sessionId);
            return;
        }

        Field field = setField(fieldCells, this);
        if (field == null) sendMessage(new Gson().toJson(Notice.OK), sessionId);
        else {
            fields.put(sessionId, field);
            sendMessage(new Gson().toJson(Notice.Error), sessionId);
        }
    }

    @Override
    public void startBattle(String sessionId) throws IOException {
        Room room = null;

        for (Room rom : fullRooms) {
            if (rom.contains(sessionId)) {
                room = rom;
                break;
            }
        }

        String nickName;
        String opponentId;

        if (room.getPlayer1().equals(sessionId)) {
            nickName = "Игрок 1";
            opponentId = room.getPlayer2();
        } else {
            nickName = "Игрок 2";
            opponentId = room.getPlayer1();
        }

        readyToBattle.put(sessionId, true);
        Player player = new Player(nickName, fields.get(sessionId));
        players.put(sessionId, player);
        turns.put(sessionId, false);

        if (readyToBattle.get(opponentId)) {
            Battle battle;

            if (room.getPlayer1().equals(sessionId))
                battle = new Battle(player, players.get(opponentId));
            else battle = new Battle(players.get(opponentId), player);

            battle.addBattleEndedListener(this);
            battle.addNextTurnListener(this);

            Thread thread = new Thread(battle);
            threads.put(sessionId, thread);
            thread.start();
        } else {
            sendMessage(new Gson().toJson(Notice.OK), sessionId);
        }
    }

    @Override
    public void shot(String message, String sessionId) throws IOException {
        if (players.containsKey(sessionId)
                && turns.containsKey(sessionId) && turns.get(sessionId)) {
            turns.put(sessionId, false);
            Coordinates coordinates;

            try {
                coordinates = new Gson().fromJson(message, Coordinates.class);
            } catch (JsonSyntaxException | JsonIOException e) {
                sendMessage(new Gson().toJson(Notice.Error), sessionId);
                return;
            }

            sendMessage(new Gson().toJson(shot(coordinates, players.get(sessionId))), sessionId);
        } else {
            sendMessage(new Gson().toJson(Notice.Error), sessionId);
        }
    }

    @Override
    public void battleEnd(Gamer winner, Gamer loser) {
        String winnerId = getSessionId(players, (Player)winner);

        Room room = null;

        for (Room rom : fullRooms) {
            if (rom.contains(winnerId)) {
                room = rom;
                break;
            }
        }

        String loserId;

        if (room.getPlayer1().equals(winnerId)) loserId = room.getPlayer2();
        else loserId = room.getPlayer1();

        try {
            sendMessage(new Gson().toJson(BattleResult.Win), winnerId);
            sendMessage(new Gson().toJson(BattleResult.Lose), loserId);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void shotInField(Field field, Coordinates hit, HashSet<Coordinates> misses) {
        if (fields.containsValue(field)) {
            String sessionId = getSessionId(fields, field);

            try {
                sendMessage(new Gson().toJson(new FieldChanges(FieldStatus.First, hit,
                        misses.toArray(new Coordinates[misses.size()]))), sessionId);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void nextTurn(Gamer gamer) {
        if (players.containsValue(gamer)) {
            String sessionId = getSessionId(players, (Player)gamer);

            turns.put(sessionId, true);

            try {
                sendMessage(new Gson().toJson(Notice.YourTurn), sessionId);
            } catch (IOException e) {
                System.err.print(e.getMessage());
            }
        }
    }
}
