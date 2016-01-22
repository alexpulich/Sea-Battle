package ru.ifmo.practice.seabattle.battle;

public interface Gamer {
    String getNickName();
    Field getInitialField();
    void youWin();
    void youLose();
    Cell nextRound(Field field);
}
