package ru.ifmo.practice.seabattle.server;

import ru.ifmo.practice.seabattle.battle.Coordinates;
import ru.ifmo.practice.seabattle.battle.Field;
import ru.ifmo.practice.seabattle.battle.Gamer;

import java.util.HashSet;

public class Player implements Gamer {
    private String nickName;
    private Field field;

    public Player(String nickName, Field field) {
        this.nickName = nickName;
        this.field = field;
    }

    @Override
    public String getNickName() {
        return nickName;
    }

    @Override
    public Field getField() {
        return field;
    }

    @Override
    public Coordinates nextRound(HashSet<Coordinates> resultOfPreviousShot) {
        return null;
    }
}
