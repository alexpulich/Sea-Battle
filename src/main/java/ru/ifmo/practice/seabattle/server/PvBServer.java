package ru.ifmo.practice.seabattle.server;

import ru.ifmo.practice.seabattle.battle.Battle;
import ru.ifmo.practice.seabattle.battle.Field;
import ru.ifmo.practice.seabattle.battle.Gamer;
import ru.ifmo.practice.seabattle.battle.bot.Bot;

import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;

@ServerEndpoint("/pvbserver")
public class PvBServer extends BattleServer {
    @OnOpen
    public void onOpen(Session session) throws IOException {
        sessions.put(session.getId(), session);
    }

    @OnClose
    public void onClose(Session session) throws IOException {
        sessions.remove(session.getId());
        players.remove(session.getId());
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
        turns.remove(session.getId());
        if (threads.containsKey(session.getId())) {
            Thread thread = threads.get(session.getId());
            thread.interrupt();
            threads.remove(session.getId());
        }
        commands.remove(session.getId());
    }

    @OnMessage
    public void onMessage(String message, Session session) throws IOException {
        parseMessage(message, session);
    }

    @Override
    public void startBattle(Session session) throws IOException {
        if (fields.containsKey(session.getId()) && !battles.containsKey(session.getId())) {
            Player player = new Player("Игрок", fields.get(session.getId()));
            Battle battle = new Battle(player, new Bot("Бот"));
            battle.addBattleEndedListener(this);
            battle.addNextTurnListener(this);

            players.put(session.getId(), player);
            battles.put(session.getId(), battle);
            turns.put(session.getId(), false);

            Thread thread = new Thread(battle);
            threads.put(session.getId(), thread);
            thread.start();
        } else {
            sendMessage(new Message<>(Notice.Error), session);
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
            sendMessage(new Message<>(result), sessions.get(sessionId));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
