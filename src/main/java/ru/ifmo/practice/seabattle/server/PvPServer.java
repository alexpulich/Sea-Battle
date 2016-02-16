package ru.ifmo.practice.seabattle.server;

import ru.ifmo.practice.seabattle.battle.Battle;
import ru.ifmo.practice.seabattle.battle.Field;
import ru.ifmo.practice.seabattle.battle.Gamer;

import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

@ServerEndpoint("/pvpserver")
public class PvPServer extends BattleServer {
    private static ArrayList<Room> freeRooms = new ArrayList<>();
    private static ArrayList<Room> fullRooms = new ArrayList<>();
    private static HashMap<String, Boolean> readyToBattle = new HashMap<>();

    @OnOpen
    public void onOpen(Session session) throws IOException {
        sessions.put(session.getId(), session);
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

            sendMessage(new Message<>(Notice.OpponentFound), sessions.get(room.getPlayer1()));
            sendMessage(new Message<>(Notice.OpponentFound), sessions.get(room.getPlayer2()));
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
        parseMessage(message, session);
    }

    @Override
    public void startBattle(Session session) throws IOException {
        Room room = null;

        for (Room rom : fullRooms) {
            if (rom.contains(session.getId())) {
                room = rom;
                break;
            }
        }

        if (fields.containsKey(session.getId()) && !battles.containsKey(session.getId()) && room != null) {
            String nickName;
            String opponentId;

            if (room.getPlayer1().equals(session.getId())) {
                nickName = "Игрок 1";
                opponentId = room.getPlayer2();
            } else {
                nickName = "Игрок 2";
                opponentId = room.getPlayer1();
            }

            readyToBattle.put(session.getId(), true);
            Player player = new Player(nickName, fields.get(session.getId()));
            players.put(session.getId(), player);
            turns.put(session.getId(), false);

            if (readyToBattle.get(opponentId)) {
                Battle battle;

                if (room.getPlayer1().equals(session.getId()))
                    battle = new Battle(player, players.get(opponentId));
                else battle = new Battle(players.get(opponentId), player);

                battle.addBattleEndedListener(this);
                battle.addNextTurnListener(this);

                Thread thread = new Thread(battle);
                threads.put(session.getId(), thread);
                thread.start();
            }
        } else sendMessage(new Message<>(Notice.Error), session);
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

        if (room != null) {

            String loserId;

            if (room.getPlayer1().equals(winnerId)) loserId = room.getPlayer2();
            else loserId = room.getPlayer1();

            try {
                sendMessage(new Message<>(BattleResult.Win), sessions.get(winnerId));
                sendMessage(new Message<>(BattleResult.Lose), sessions.get(loserId));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
