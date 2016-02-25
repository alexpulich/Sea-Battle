package ru.ifmo.practice.seabattle.battle;

import ru.ifmo.practice.seabattle.exceptions.BattleNotFinishedException;
import ru.ifmo.practice.seabattle.exceptions.IllegalNumberOfShipException;

import java.util.HashSet;

public class Battle implements Runnable {
    private HashSet<BattleEndedListener> battleEndedListeners = new HashSet<>();
    private HashSet<NextTurnListener> nextTurnListeners = new HashSet<>();
    private Gamer firstGamer;
    private Gamer secondGamer;
    private Gamer winner;
    private Gamer looser;

    public Gamer getWinner() throws BattleNotFinishedException {
        if (winner == null) throw new BattleNotFinishedException();
        else return winner;
    }

    public Gamer getLoser() throws BattleNotFinishedException {
        if (looser == null) throw new BattleNotFinishedException();
        else return looser;
    }

    public Battle(Gamer firstGamer, Gamer secondGamer) {
        this.firstGamer = firstGamer;
        this.secondGamer = secondGamer;
        winner = null;
        looser = null;
    }

    public void start() throws IllegalNumberOfShipException {
        Gamer attacker = firstGamer;
        Gamer defender = secondGamer;

        do {
            fireNextTurnListeners(attacker);
            Coordinates shot = attacker.getShot();
            HashSet<Coordinates> shotResult;
            try {
                shotResult = defender.getFirstField().shot(shot);
            } catch (NullPointerException e) {
                return;
            }
            attacker.setLastRoundResult(shotResult);

            if (defender.getFirstField().getNumberOfDestroyedDecks() == 20) {
                winner = attacker;
                looser = defender;
            } else if (shotResult == null) {
                Gamer foo = attacker;
                attacker = defender;
                defender = foo;
            }
        } while (winner == null);

        fireBattleEndedListeners();
    }

    public void addBattleEndedListener(BattleEndedListener listener) {
        battleEndedListeners.add(listener);
    }

    public void addNextTurnListener(NextTurnListener listener) {
        nextTurnListeners.add(listener);
    }

    public boolean removeBattleEndedListener(BattleEndedListener listener) {
        return battleEndedListeners.remove(listener);
    }

    public boolean removeNextTurnListener(NextTurnListener listener) {
        return nextTurnListeners.remove(listener);
    }

    private void fireBattleEndedListeners() {
        battleEndedListeners.forEach((listener) -> listener.battleEnd(winner, looser));
    }

    private void fireNextTurnListeners(Gamer gamer) {
        nextTurnListeners.forEach((listener) -> listener.nextTurn(gamer));
    }

    @Override
    public void run() {
        this.start();
    }
}
