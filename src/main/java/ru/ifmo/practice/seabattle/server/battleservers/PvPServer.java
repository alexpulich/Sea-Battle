package ru.ifmo.practice.seabattle.server.battleservers;

import ru.ifmo.practice.seabattle.battle.Battle;
import ru.ifmo.practice.seabattle.battle.Gamer;
import ru.ifmo.practice.seabattle.battle.SecondField;
import ru.ifmo.practice.seabattle.exceptions.BattleAlreadyStartException;
import ru.ifmo.practice.seabattle.exceptions.RoomIsFullException;
import ru.ifmo.practice.seabattle.server.Message;
import ru.ifmo.practice.seabattle.server.Notice;

import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@ServerEndpoint(value = "/pvpserver")
public class PvPServer extends BattleServer {
    private static ArrayList<String> queue = new ArrayList<>();
    private static ArrayList<Room> rooms = new ArrayList<>();
    private static HashMap<String, Timer> timers = new HashMap<>();
    private static Lock lock = new ReentrantLock();

    @OnOpen
    public void onOpen(Session session) throws IOException {
        super.onOpen(session, ((PrincipalWithSession) session.getUserPrincipal()).getSession());

        queue.add(session.getId());

        Timer timer = new Timer();
        timers.put(session.getId(), timer);

        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                lock.lock();
                try {
                    if (queue.contains(session.getId())) {
                        if (queue.size() > 1) {
                            queue.remove(session.getId());
                            String opponentId = getOpponentByRating(players.get(session.getId()));
                            queue.remove(opponentId);
                            rooms.add(new Room(session.getId(), opponentId));

                            try {
                                sendMessage(new Message<>(Notice.OpponentFound), session);
                                sendMessage(new Message<>(Notice.OpponentFound), players.get(opponentId).getSession());
                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                            timers.remove(session.getId());
                            this.cancel();
                        }
                    } else {
                        timers.remove(session.getId());
                        this.cancel();
                    }
                } finally {
                    lock.unlock();
                }
            }
        };

        timer.schedule(task, 7000, 7000);
    }

    @Override
    @OnClose
    public void onClose(Session session) throws IOException {
        super.onClose(session);

        queue.remove(session.getId());

        if (timers.containsKey(session.getId())) {
            timers.get(session.getId()).cancel();
            timers.remove(session.getId());
        }

        boolean isContains = false;

        Iterator<Room> iterator = rooms.iterator();
        while (!isContains && iterator.hasNext()) {
            Room room = iterator.next();
            if (room.contains(session.getId())) {
                Player opponent;
                if (room.getPlayer1().equals(session.getId()))
                    opponent = players.get(room.getPlayer2());
                else opponent = players.get(room.getPlayer1());
                sendMessage(new Message<>(Notice.OpponentLeft), opponent.getSession());

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
    public void startBattle(Player player) throws IOException, BattleAlreadyStartException {
        Room room = null;

        for (Room rom : rooms) {
            if (rom.contains(player.getSession().getId())) {
                room = rom;
                break;
            }
        }

        if (player.getFirstFieldBuilder() != null && !player.isInBattle() && room != null) {
            if (player.getFirstField() != null)
                player.getFirstField().removeChangesListener(this);

            player.createFirstField();
            player.getFirstField().addChangesListener(this);

            SecondField secondField = new SecondField();

            player.setSecondField(secondField);
            player.readyToBattle();

            secondField.addChangesListener(this);

            Player opponent;

            if (room.getPlayer1().equals(player.getSession().getId()))
                opponent = players.get(room.getPlayer2());
            else opponent = players.get(room.getPlayer1());

            if (opponent.isReadyToBattle()) {
                Battle battle;

                if (room.getPlayer1().equals(player.getSession().getId()))
                    battle = new Battle(player, opponent);
                else battle = new Battle(opponent, player);

                Thread thread = new Thread(battle);
                BattleInfo battleInfo = new BattleInfo(battle, thread);

                player.setBattleInfo(battleInfo);
                opponent.setBattleInfo(battleInfo);

                battle.addBattleEndedListener(this);
                battle.addNextTurnListener(this);

                thread.start();
            }
        } else if (player.isInBattle()) throw new BattleAlreadyStartException();
        else throw new IllegalArgumentException();
    }

    @Override
    public void battleEnd(Gamer winner, Gamer loser) {
        try {
            sendMessage(new Message<>(BattleResult.Win), ((Player) winner).getSession());
            sendMessage(new Message<>(BattleResult.Lose), ((Player) loser).getSession());

            if (((Player) winner).getId() != null || ((Player) loser).getId() != null) {
                int winnerId = BattleResultHandler.UNREGISTRED_ID, loserId = BattleResultHandler.UNREGISTRED_ID;
                if (((Player) winner).getId() != null) winnerId = ((Player) winner).getId();
                if (((Player) loser).getId() != null) loserId = ((Player) loser).getId();
                new BattleResultHandler().handle(winnerId, loserId);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String getOpponentByRating(Player player) {
        if (queue.size() == 1) return queue.get(0);

        ArrayList<String> ids = new ArrayList<>();
        ids.addAll(queue);
        ids.sort((id1, id2) -> {
            Player player1 = players.get(id1);
            Player player2 = players.get(id2);

            if (player1.getRating() == null && player2.getRating() == null) return 0;
            else {
                if (player1.getRating() == null || player2.getRating() == null) {
                    if (player1.getRating() == null) return -1;
                    else return 1;
                } else {
                    if (player1.getRating() > player2.getRating()) return 1;
                    else if (player1.getRating() < player2.getRating()) return -1;
                    else return 0;
                }
            }
        });

        if (player.getRating() == null) return ids.get(0);
        else {
            for (int i = ids.size() - 1; i > 0; i--) {
                Player leftOpponent = players.get(ids.get(i - 1));
                Player rightOpponent = players.get(ids.get(i));

                if (rightOpponent.getRating() == null
                        || rightOpponent.getRating() <= player.getRating()) return ids.get(i);
                else {
                    if (leftOpponent.getRating() != null) {
                        if (leftOpponent.getRating() <= player.getRating()) {
                            if (getRatingDiff(player, leftOpponent) < getRatingDiff(player, rightOpponent))
                                return ids.get(i - 1);
                            else return ids.get(i);
                        }
                    } else ids.get(i);
                }
            }

            return ids.get(ids.size() - 1);
        }
    }

    private int getRatingDiff(Player player, Player opponent) {
        return Math.abs(player.getRating() - opponent.getRating());
    }

    @Override
    public GamersInformation getGamersInformation(Player player) {
        Player opponent = null;

        for (Room room : rooms) {
            if (room.contains(player.getSession().getId())) {
                if (room.getPlayer1().equals(player.getSession().getId()))
                    opponent = players.get(room.getPlayer2());
                else opponent = players.get(room.getPlayer1());

                break;
            }
        }

        if (opponent != null) {
            return new GamersInformation(player.getNickName(), opponent.getNickName(),
                    player.getRating(), opponent.getRating());
        } else throw new IllegalArgumentException();
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
