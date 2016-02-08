package ru.ifmo.practice.seabattle.battle;

import ru.ifmo.practice.seabattle.exceptions.BattleNotFinishedException;

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

    public void start() {
        
    }
}
