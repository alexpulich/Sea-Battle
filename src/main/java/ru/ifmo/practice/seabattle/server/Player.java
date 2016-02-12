package ru.ifmo.practice.seabattle.server;

import ru.ifmo.practice.seabattle.battle.Coordinates;
import ru.ifmo.practice.seabattle.battle.Field;
import ru.ifmo.practice.seabattle.battle.Gamer;

import java.util.HashSet;

class Player implements Gamer {
    private String nickName;
    private Field field;
    private Coordinates shot = null;
    private HashSet<Coordinates> shotResult = null;
    private boolean firstTurn = true;

    void setShot(Coordinates shot) {
        this.shot = shot;
    }

    void setShotResult(HashSet<Coordinates> shotResult) {
        this.shotResult = shotResult;
    }

    public HashSet<Coordinates> getShotResult() {
        return shotResult;
    }

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
        if (!firstTurn) {
            if (resultOfPreviousShot == null) shotResult = new HashSet<>();
            else shotResult = resultOfPreviousShot;
        }

        while (this.shot == null) {
            try {
                wait(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        Coordinates shot = this.shot;
        this.shot = null;
        firstTurn = false;

        return shot;
    }
}
