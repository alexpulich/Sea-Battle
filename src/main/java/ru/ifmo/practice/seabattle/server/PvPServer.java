package ru.ifmo.practice.seabattle.server;

import com.google.gson.Gson;
import ru.ifmo.practice.seabattle.battle.Battle;
import ru.ifmo.practice.seabattle.battle.Coordinates;
import ru.ifmo.practice.seabattle.battle.Field;
import ru.ifmo.practice.seabattle.battle.Gamer;

import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.*;

@ServerEndpoint("/pvpserver")
public class PvPServer extends Server implements SeaBattleServer {
    private static ArrayList<Room> freeRooms = new ArrayList<>();
    private static ArrayList<Room> fullRooms = new ArrayList<>();
    private static HashMap<String, Session> sessions = new HashMap<>();
    private static HashMap<String, Player> players = new HashMap<>();
    private static HashMap<String, Battle> battles = new HashMap<>();
    private static HashMap<String, Command> commands = new HashMap<>();
    private static HashMap<String, Field> fields = new HashMap<>();
    private static HashMap<String, Boolean> readyToBattle = new HashMap<>();

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
        sessions.remove(session.getId());
        if (players.containsKey(session.getId())) {
            players.remove(session.getId());
        }
        if (battles.containsKey(session.getId())) {
            Battle battle = battles.get(session.getId());
            battle.removeListener(this);
            battles.remove(session.getId());
        }
        if (fields.containsKey(session.getId())) {
            Field field = fields.get(session.getId());
            field.removeShotListener(this);
            fields.remove(session.getId());
        }
        commands.remove(session.getId());
        if (readyToBattle.containsKey(session.getId()))
            readyToBattle.remove(session.getId());

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
            if (iterator.next().contains(session.getId())) {
                iterator.remove();
                isContains = true;
            }
        }
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
    public void placeShipsRandom(String sessionID) {
        fields.put(sessionID, placeShipsRandom(this));
    }

    @Override
    public void setField(String message, String sessionId) throws IOException {
        Field field = setField(message, this);
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

        readyToBattle.replace(sessionId, false, true);
        Player player = new Player(nickName, fields.get(sessionId));
        players.put(sessionId, player);

        if (readyToBattle.get(opponentId)) {
            Battle battle;

            if (room.getPlayer1().equals(sessionId)) {
                battle = new Battle(player, players.get(opponentId));

                sendMessage(new Gson().toJson(Notice.FirstTurn), sessionId);
                sendMessage(new Gson().toJson(Notice.SecondTurn), opponentId);
            } else {
                battle = new Battle(players.get(opponentId), player);

                sendMessage(new Gson().toJson(Notice.FirstTurn), opponentId);
                sendMessage(new Gson().toJson(Notice.SecondTurn), sessionId);
            }

            battle.addListener(this);
            battle.start();
        }
    }

    @Override
    public void shot(String message, String sessionId) throws IOException {
        if (players.containsKey(sessionId)) {
            sendMessage(new Gson().toJson(shot(message, players.get(sessionId))), sessionId);
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
    public void shot(Field field, Coordinates hit, HashSet<Coordinates> misses) {
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
}
