package ru.ifmo.practice.seabattle.server;

import com.google.gson.Gson;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;
import ru.ifmo.practice.seabattle.battle.*;
import ru.ifmo.practice.seabattle.battle.bot.Bot;

import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;

@ServerEndpoint("/pvbserver")
public class PvBServer extends Server implements BattleServer {
    private static HashMap<String, Session> sessions = new HashMap<>();
    private static HashMap<String, Player> players = new HashMap<>();
    private static HashMap<String, Battle> battles = new HashMap<>();
    private static HashMap<String, Command> commands = new HashMap<>();
    private static HashMap<String, Field> fields = new HashMap<>();
    private static HashMap<String, Boolean> turns = new HashMap<>();
    private static HashMap<String, Thread> threads = new HashMap<>();

    @OnOpen
    public void onOpen(Session session) throws IOException {
        sessions.put(session.getId(), session);
        commands.put(session.getId(), null);
    }

    @OnClose
    public void onClose(Session session) throws IOException {
        sessions.remove(session.getId());
        if (players.containsKey(session.getId())) {
            players.remove(session.getId());
        }
        if (battles.containsKey(session.getId())) {
            Battle battle = battles.get(session.getId());
            battle.removeBattleEndedListener(this);
            battles.remove(session.getId());
        }
        if (fields.containsKey(session.getId())) {
            Field field = fields.get(session.getId());
            field.removeShotListener(this);
            fields.remove(session.getId());
        }
        if (turns.containsKey(session.getId())) {
            turns.remove(session.getId());
        }
        if (threads.containsKey(session.getId())) {
            Thread thread = threads.get(session.getId());
            thread.interrupt();
            threads.remove(session.getId());
        }
        commands.remove(session.getId());
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
        if (field == null) sendMessage(new Gson().toJson(Notice.Error), sessionId);
        else {
            fields.put(sessionId, field);
            sendMessage(new Gson().toJson(Notice.OK), sessionId);
        }
    }

    @Override
    public void startBattle(String sessionId) throws IOException {
        if (fields.containsKey(sessionId)) {
            Player player = new Player("Игрок", fields.get(sessionId));
            Battle battle = new Battle(player, new Bot("Бот"));
            battle.addBattleEndedListener(this);
            battle.addNextTurnListener(this);

            players.put(sessionId, player);
            battles.put(sessionId, battle);
            turns.put(sessionId, false);

            Thread thread = new Thread(battle);
            threads.put(sessionId, thread);
            thread.start();
        } else {
            sendMessage(new Gson().toJson(Notice.Error), sessionId);
        }
    }

    @Override
    public void shot(String message, String sessionId) throws IOException {
        if (players.containsKey(sessionId) && turns.containsKey(sessionId) && turns.get(sessionId)) {
            turns.put(sessionId, false);
            Coordinates coordinates;

            try {
                coordinates = new Gson().fromJson(message, Coordinates.class);
            } catch (JsonSyntaxException | JsonIOException e) {
                sendMessage(new Gson().toJson(Notice.Error), sessionId);
                return;
            }

            sendMessage(new Gson().toJson(shot(coordinates, players.get(sessionId))), sessionId);
        } else sendMessage(new Gson().toJson(Notice.Error), sessionId);
    }

    @Override
    public void shotInField(Field field, Coordinates hit, HashSet<Coordinates> misses) {
        if (fields.containsValue(field)) {
            String sessionId = getSessionId(fields, field);

            try {
                sendMessage(new Gson().toJson(new FieldChanges(FieldStatus.First, hit,
                        misses.toArray(new Coordinates[misses.size()]))), sessionId);
            } catch (IOException e) {
                System.err.print(e.getMessage());
            }
        }
    }

    @Override
    public void battleEnd(Gamer winner, Gamer loser) {
        Gamer gamer;
        BattleResult result;

        if (players.containsValue(winner)) {
            gamer = winner;
            result = BattleResult.Win;
        }
        else if (players.containsValue(loser)) {
            gamer = loser;
            result = BattleResult.Lose;
        }
        else return;

        String sessionId = getSessionId(players, (Player)gamer);

        try {
            sendMessage(new Gson().toJson(result), sessionId);
        } catch (IOException e) {
            e.printStackTrace();
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
                e.printStackTrace();
            }
        }
    }
}
