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

    synchronized void setShot(Coordinates shot) {
        this.shot = shot;

        this.notifyAll();

        try {
            if (shotResult == null) this.wait();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    synchronized void setShotResult(HashSet<Coordinates> shotResult) {
        this.shotResult = shotResult;
    }

    synchronized public HashSet<Coordinates> getShotResult() {
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
    public synchronized Coordinates nextRound(HashSet<Coordinates> resultOfPreviousShot) {
        if (resultOfPreviousShot == null) shotResult = new HashSet<>();
        else shotResult = resultOfPreviousShot;

        this.notifyAll();

        try {
            if (shot == null) this.wait();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        Coordinates shot = this.shot;
        this.shot = null;

        return shot;
    }
}
