package ru.ifmo.practice.seabattle.server.battleservers;

import ru.ifmo.practice.seabattle.battle.Battle;
import ru.ifmo.practice.seabattle.battle.Gamer;
import ru.ifmo.practice.seabattle.battle.SecondField;
import ru.ifmo.practice.seabattle.battle.bot.Bot;
import ru.ifmo.practice.seabattle.exceptions.BattleAlreadyStartException;
import ru.ifmo.practice.seabattle.server.Message;

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
    public void startBattle(Player player) throws IOException, BattleAlreadyStartException {
        if (player.getFirstFieldBuilder() != null && !player.isInBattle()) {
            if (player.getFirstField() != null)
                player.getFirstField().removeChangesListener(this);

            player.createFirstField();
            player.getFirstField().addChangesListener(this);

            SecondField secondField = new SecondField();

            player.setSecondField(secondField);

            secondField.addChangesListener(this);

            Battle battle = new Battle(player, new Bot("Бот"));
            Thread thread = new Thread(battle, "Битва");

            player.setBattleInfo(new BattleInfo(battle, thread));

            battle.addBattleEndedListener(this);
            battle.addNextTurnListener(this);
            thread.start();
        } else if (player.isInBattle()) throw new BattleAlreadyStartException();
        else throw new IllegalArgumentException();
    }

    @Override
    public void battleEnd(Gamer winner, Gamer loser) {
        Player player;
        BattleResult result;
        BattleResultHandler battleResultHandler = new BattleResultHandler();

        if (winner instanceof Player) {
            player = (Player)winner;
            result = BattleResult.Win;

            if (player.getId() != null) battleResultHandler.handle(player.getId(), BattleResultHandler.BOT_ID);
        }
        else if (loser instanceof Player) {
            player = (Player)loser;
            result = BattleResult.Lose;

            if (player.getId() != null) battleResultHandler.handle(BattleResultHandler.BOT_ID, player.getId());
        }
        else return;

        try {
            sendMessage(new Message<>(result), player.getSession());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
