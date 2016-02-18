package ru.ifmo.practice.seabattle.server;

import ru.ifmo.practice.seabattle.battle.Battle;
import ru.ifmo.practice.seabattle.battle.Gamer;
import ru.ifmo.practice.seabattle.battle.SecondField;
import ru.ifmo.practice.seabattle.exceptions.FieldAlreadySetException;
import ru.ifmo.practice.seabattle.exceptions.RoomIsFullException;

import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

@ServerEndpoint(value = "/pvpserver")
public class PvPServer extends BattleServer {
    private static ArrayList<Room> freeRooms = new ArrayList<>();
    private static ArrayList<Room> fullRooms = new ArrayList<>();
    private static HashMap<String, Boolean> readyToBattle = new HashMap<>();

    @OnOpen
    public void onOpen(Session session) throws IOException {
        super.onOpen(session, ((PrincipalWithSession) session.getUserPrincipal()).getSession());

        if (freeRooms.isEmpty()) {
            Room room = new Room();
            room.add(session.getId());
            freeRooms.add(room);
        } else {
            Room room = freeRooms.get(0);
            freeRooms.remove(room);
            room.add(session.getId());
            fullRooms.add(room);

            Player player1 = players.get(room.getPlayer1());
            Player player2 = players.get(room.getPlayer2());

            sendMessage(new Message<>(Notice.OpponentFound), player1.getSession());
            sendMessage(new Message<>(Notice.OpponentFound), player2.getSession());
        }
    }

    @Override
    @OnClose
    public void onClose(Session session) throws IOException {
        super.onClose(session);

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

                Session opponent = players.get(opponentId).getSession();
                super.onClose(opponent);
                opponent.close();

                iterator.remove();
                isContains = true;
            }
        }
    }

    @Override
    @OnMessage
    public void onMessage(String message, Session session) throws IOException {
        super.onMessage(message, session);
    }

    @Override
    public void startBattle(Session session) throws IOException {
        Player player = players.get(session.getId());

        Room room = null;

        for (Room rom : fullRooms) {
            if (rom.contains(session.getId())) {
                room = rom;
                break;
            }
        }

        if (player.getFirstField() != null && !player.isInBattle() && room != null) {
            SecondField secondField = new SecondField();

            try {
                player.setSecondField(secondField);
                player.readyToBattle();
            } catch (FieldAlreadySetException e) {
                sendMessage(new Message<>(Notice.Error), session);
            }

            secondField.addChangesListener(this);

            Player opponent;

            if (room.getPlayer1().equals(player.getSession().getId()))
                opponent = players.get(room.getPlayer2());
            else opponent = players.get(room.getPlayer1());

            if (opponent.isReadyToBattle()) {
                Battle battle;

                if (room.getPlayer1().equals(session.getId()))
                    battle = new Battle(player, opponent);
                else battle = new Battle(opponent, player);

                Thread thread = new Thread(battle);
                BattleInfo battleInfo = new BattleInfo(battle, thread);

                try {
                    player.setBattleInfo(battleInfo);
                    opponent.setBattleInfo(battleInfo);
                } catch (FieldAlreadySetException e) {
                    sendMessage(new Message<>(Notice.Error), session);
                }

                battle.addBattleEndedListener(this);
                battle.addNextTurnListener(this);

                thread.start();
            }
        } else sendMessage(new Message<>(Notice.Error), session);
    }

    @Override
    public void battleEnd(Gamer winner, Gamer loser) {
        try {
            sendMessage(new Message<>(BattleResult.Win), ((Player) winner).getSession());
            sendMessage(new Message<>(BattleResult.Lose), ((Player) loser).getSession());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private class Room {
        private String player1 = null;
        private String player2 = null;

        public String getPlayer1() {
            return player1;
        }

        public String getPlayer2() {
            return player2;
        }

        public boolean isFull() {
            return player1 != null && player2 != null;
        }

        public boolean isEmpty() {
            return player1 == null && player2 == null;
        }

        public Room() {}

        public Room(String player1, String player2) {
            this.player1 = player1;
            this.player2 = player2;
        }

        public void add(String player) {
            if (player1 == null) player1 = player;
            else if (player2 == null) player2 = player;
            else throw new RoomIsFullException();
        }

        public boolean remove(String player) {
            if (player.equals(player1)) {
                player1 = null;
                return true;
            } else if (player.equals(player2)) {
                player2 = null;
                return true;
            } else return false;
        }

        public boolean contains(String player) {
            return player.equals(player1) || player.equals(player2);
        }
    }
}
