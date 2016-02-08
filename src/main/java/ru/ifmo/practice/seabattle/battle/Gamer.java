package ru.ifmo.practice.seabattle.battle;

import java.util.HashSet;

public interface Gamer {
    String getNickName();
    Field getField();
    Coordinates nextRound(HashSet<Coordinates> resultOfPreviousShot,
                          HashSet<Coordinates> resultOfOpponentShot);
}
