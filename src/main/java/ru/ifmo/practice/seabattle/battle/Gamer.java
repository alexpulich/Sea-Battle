package ru.ifmo.practice.seabattle.battle;

import java.util.HashSet;

public interface Gamer {
    String getNickName();
    FirstField getFirstField();
    void setLastRoundResult(HashSet<Coordinates> resultOfPreviousShot);
    Coordinates getShot();
}