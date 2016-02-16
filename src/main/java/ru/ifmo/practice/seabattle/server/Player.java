package ru.ifmo.practice.seabattle.server;

import ru.ifmo.practice.seabattle.battle.*;

import java.util.HashMap;
import java.util.HashSet;

class Player implements Gamer {
    private String nickName;
    private FirstField firstField;
    private SecondField secondField;
    private Coordinates shot = null;
    private Coordinates lastShot = null;
    private boolean firstTurn = true;
    private HashSet<Coordinates> resultOfPreviousShot = null;
    private HashSet<Coordinates> blackList = new HashSet<>();

    synchronized void setShot(Coordinates shot) {
        if (!blackList.contains(shot)) {
            blackList.add(new Coordinates(shot.getX(), shot.getY()));
            Log.getInstance().sendMessage(this.getClass(), "Выстрел установлен");
            this.shot = shot;
            this.notifyAll();
        } else throw new IllegalArgumentException("В данную клетку уже стреляли");
    }

    public Player(String nickName, FirstField firstField, SecondField secondField) {
        this.nickName = nickName;
        this.firstField = firstField;
        this.secondField = secondField;
    }

    @Override
    public String getNickName() {
        return nickName;
    }

    @Override
    public FirstField getFirstField() {
        return firstField;
    }

    @Override
    synchronized public void setLastRoundResult(HashSet<Coordinates> resultOfPreviousShot) {
        HashMap<Coordinates, Cell> secondFieldChanges = new HashMap<>();

        if (!firstTurn) {
            if (resultOfPreviousShot == null) secondFieldChanges.put(lastShot, Cell.Miss);
            else {
                secondFieldChanges.put(lastShot, Cell.Hit);
                if (resultOfPreviousShot.size() > 1)
                    resultOfPreviousShot.forEach((coordinates) -> {
                        if (!coordinates.equals(lastShot)) {
                            secondFieldChanges.put(coordinates, Cell.Miss);
                        }
                    });
            }
            secondField.change(secondFieldChanges);
            Log.getInstance().sendMessage(this.getClass(), "Второе поле" + nickName + "изменено");
        }

        this.resultOfPreviousShot = resultOfPreviousShot;
    }

    @Override
    synchronized public Coordinates getShot() {
        try {
            if (shot == null) {
                Log.getInstance().sendMessage(this.getClass(), "Ожидаем выстрела");
                this.wait();
                Log.getInstance().sendMessage(this.getClass(), "Выстрел произведен");
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        lastShot = new Coordinates(shot.getX(), shot.getY());
        shot = null;
        firstTurn = false;

        return new Coordinates(lastShot.getX(), lastShot.getY());
    }

    public SecondField getSecondField() {
        return secondField;
    }
}
