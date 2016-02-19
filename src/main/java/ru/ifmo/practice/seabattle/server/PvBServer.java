package ru.ifmo.practice.seabattle.server;

import ru.ifmo.practice.seabattle.battle.Battle;
import ru.ifmo.practice.seabattle.battle.Gamer;
import ru.ifmo.practice.seabattle.battle.SecondField;
import ru.ifmo.practice.seabattle.battle.bot.Bot;
import ru.ifmo.practice.seabattle.exceptions.FieldAlreadySetException;
import ru.ifmo.practice.seabattle.exceptions.IllegalNumberOfShipException;

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
        super.onOpen(session, ((PrincipalWithSession) session.getUserPrincipal()).getSession());
    }

    @Override
    @OnClose
    public void onClose(Session session) throws IOException {
        super.onClose(session);
    }

    @Override
    @OnMessage
    public void onMessage(String message, Session session) throws IOException {
        super.onMessage(message, session);
    }

    @Override
    public void startBattle(Session session) throws IOException {
        Player player = players.get(session.getId());

        try {
            setFirstField(player);
        } catch (FieldAlreadySetException | IllegalArgumentException
                | IllegalNumberOfShipException e) {
            sendMessage(new Message<>(Notice.Error), session);
            return;
        }

        if (player.getFirstField() != null && !player.isInBattle()) {
            SecondField secondField = new SecondField();

            try {
                player.setSecondField(secondField);
            } catch (FieldAlreadySetException e) {
                sendMessage(new Message<>(Notice.Error), session);
            }

            secondField.addChangesListener(this);

            Battle battle = new Battle(player, new Bot("Бот"));
            Thread thread = new Thread(battle, "Битва");

            try {
                player.setBattleInfo(new BattleInfo(battle, thread));
            } catch (FieldAlreadySetException e) {
                sendMessage(new Message<>(Notice.Error), session);
            }

            battle.addBattleEndedListener(this);
            battle.addNextTurnListener(this);
            thread.start();
        } else {
            sendMessage(new Message<>(Notice.Error), session);
        }
    }

    @Override
    public void battleEnd(Gamer winner, Gamer loser) {
        Player player;
        BattleResult result;

        if (winner instanceof Player) {
            player = (Player)winner;
            result = BattleResult.Win;
        }
        else if (loser instanceof Player) {
            player = (Player)loser;
            result = BattleResult.Lose;
        }
        else return;

        try {
            sendMessage(new Message<>(result), player.getSession());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
