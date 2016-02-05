package ru.ifmo.practice.seabattle.battle;

public interface Gamer {
    String getNickName();
    Field getCurrentField();
    Coordinates nextRound(CellStatus lastRoundResult);
}
