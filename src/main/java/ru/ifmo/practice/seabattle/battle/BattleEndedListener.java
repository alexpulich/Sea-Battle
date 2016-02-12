package ru.ifmo.practice.seabattle.battle;

public interface BattleEndedListener {
    void battleEnd(Gamer winner, Gamer loser);
}
