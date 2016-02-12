package ru.ifmo.practice.seabattle.server;

import com.google.gson.Gson;
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
import java.util.Map;
import java.util.Set;

@ServerEndpoint("/pvbserver")
public class PvBServer extends Server implements SeaBattleServer {
    private static HashMap<String, Session> sessions = new HashMap<>();
    private static HashMap<String, Player> players = new HashMap<>();
    private static HashMap<String, Battle> battles = new HashMap<>();
    private static HashMap<String, Command> commands = new HashMap<>();
    private static HashMap<String, Field> fields = new HashMap<>();

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
        if (battles.containsKey(session.getId())) battles.remove(session.getId());
        if (fields.containsKey(session.getId())) {
            Field field = fields.get(session.getId());
            field.removeShotListener(this);
            fields.remove(session.getId());
        }
        commands.remove(session.getId());
    }

    @OnMessage
    public void onMessage(String message, Session session) throws IOException {
        commands.put(session.getId(),
                parseMessage(commands.get(session.getId()), message, this, session.getId()));
    }

    private void sendMessage(String message, String sessionId) throws IOException {
        sendMessage(message, sessions.get(sessionId));
    }

    @Override
    public void placeShipsRandom(String sessionID) {
        fields.put(sessionID, placeShipsRandom(this));
    }

    @Override
    public void setField(String message, String sessionId) throws IOException {
        Field field = setField(message, this);
        if (field == null) sendMessage(new Gson().toJson(Notice.PlacementError), sessionId);
        else {
            fields.put(sessionId, field);
            sendMessage(new Gson().toJson(Notice.ShipsPlaced), sessionId);
        }
    }

    @Override
    public void startBattle(String sessionId) {
        if (fields.containsKey(sessionId)) {
            Player player = new Player("Игрок", fields.get(sessionId));
            players.put(sessionId, player);
            Battle battle = new Battle(player, new Bot("Бот"));
            battles.put(sessionId, battle);
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
    public void shot(Field field, Coordinates hit, HashSet<Coordinates> misses) {
        if (fields.containsValue(field)) {
            String sessionId = "";
            Set<Map.Entry<String, Field>> entrySet = fields.entrySet();
            for (Map.Entry<String, Field> entry : entrySet) {
                if (entry.getValue().equals(field)) {
                    sessionId = entry.getKey();
                    break;
                }
            }

            FieldChanges fieldChanges = new FieldChanges(FieldStatus.First, hit,
                    misses.toArray(new Coordinates[misses.size()]));

            try {
                sendMessage(new Gson().toJson(fieldChanges), sessionId);
            } catch (IOException e) {
                e.printStackTrace();
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

        String sessionId = "";
        Set<Map.Entry<String, Player>> entrySet = players.entrySet();
        for (Map.Entry<String, Player> entry : entrySet) {
            if (entry.getValue().equals(gamer)) {
                sessionId = entry.getKey();
                break;
            }
        }

        try {
            sendMessage(new Gson().toJson(result), sessionId);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
