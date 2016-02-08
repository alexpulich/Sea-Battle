package ru.ifmo.practice.seabattle.battle;

import ru.ifmo.practice.seabattle.exceptions.BattleNotFinishedException;
import ru.ifmo.practice.seabattle.exceptions.IllegalNumberOfShipException;

import java.util.HashSet;

public class Battle {
    private Gamer firstGamer;
    private Gamer secondGamer;
    private Gamer winner;
    private Gamer looser;

    public Gamer getWinner() throws BattleNotFinishedException {
        if (winner == null) throw new BattleNotFinishedException();
        else return winner;
    }

    public Gamer getLooser() throws BattleNotFinishedException {
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
        HashSet<Coordinates> shotResult = null;

        do {
            Coordinates shot = attacker.nextRound(shotResult);
            shotResult = defender.getField().shot(shot);

            if (defender.getField().getNumberOfNotDestroyedDecks() == 20) {
                winner = attacker;
                looser = defender;
            } else {
                Gamer foo = attacker;
                attacker = defender;
                defender = foo;
            }
        } while (winner == null);
    }
}
