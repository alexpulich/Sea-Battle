package ru.ifmo.practice.seabattle.battle.bot;

import ru.ifmo.practice.seabattle.battle.Coordinates;
import ru.ifmo.practice.seabattle.battle.Field;
import ru.ifmo.practice.seabattle.battle.FieldBuilder;
import ru.ifmo.practice.seabattle.battle.Gamer;
import ru.ifmo.practice.seabattle.exceptions.IllegalNumberOfShipException;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;

public class Bot implements Gamer {
    private Field field;
    private HashSet<Coordinates> blackList = new HashSet<>();
    private Coordinates lastShot = null;
    private HashSet<Coordinates> lastHits = new HashSet<>();
    private String nickname;

    public Bot(String nickname) {
        this.nickname = nickname;
        FieldBuilder builder = new FieldBuilder();
        builder.placeShipsRandom();
        try {
            field = builder.create();
        } catch (IllegalNumberOfShipException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String getNickName() {
        return nickname;
    }

    @Override
    public Field getField() {
        return field;
    }

    @Override
    public Coordinates nextRound(HashSet<Coordinates> resultOfPreviousShot) {
        Coordinates shot;

        if (lastHits.isEmpty()) {
            if (resultOfPreviousShot == null) {
                if (lastShot != null) blackList.add(lastShot);
                shot = randomShot();
            } else if (resultOfPreviousShot.size() == 1) {
                blackList.add(lastShot);
                lastHits.add(lastShot);
                shot = afterHitShot();
            } else {
                addToBlackList(resultOfPreviousShot);
                shot = randomShot();
            }
        }
        else {
            if (resultOfPreviousShot == null) {
                blackList.add(lastShot);
                shot = afterHitShot();
            } else if (resultOfPreviousShot.size() == 1) {
                blackList.add(lastShot);
                lastHits.add(lastShot);
                shot = afterHitShot();
            } else {
                addToBlackList(resultOfPreviousShot);
                lastHits.clear();
                shot = randomShot();
            }
        }

        lastShot = shot;
        return shot;
    }

    private void addToBlackList(HashSet<Coordinates> coordinates) {
        coordinates.forEach((coords) -> {
            if (!blackList.contains(coords)) blackList.add(coords);
        });
    }

    private Coordinates randomShot() {
        Coordinates randomShot;
        Random random = new Random();

        do {
            randomShot = new Coordinates(random.nextInt(10), random.nextInt(10));
        } while (blackList.contains(randomShot));

        return randomShot;
    }

    private Coordinates afterHitShot() {
        Coordinates shot;

        if (lastHits.size() == 1) {
            Random random = new Random();
            int x, y;
            do {
                int sign = random.nextInt(2);
                sign = sign == 1 ? -1 : 1;

                x = random.nextInt(2);
                y = 0;

                if (x == 1) x *= sign;
                else y = sign;

                for (Coordinates coordinates : lastHits) {
                    x += coordinates.getX();
                    y += coordinates.getY();
                }

                shot = new Coordinates(x, y);
            } while (blackList.contains(shot) || x < 0 || y < 0 || x > 9 || y > 9);
        } else {
            ArrayList<Coordinates> hits = new ArrayList<>();
            hits.addAll(lastHits);
            hits.sort((first, second) -> {
                if (first.getX() > second.getX() || first.getY() > second.getY()) return 1;
                else if (first.getX() < second.getX() || first.getY() < second.getY()) return -1;
                else return 0;
            });

            int x = hits.get(0).getX() * 2 - hits.get(1).getX();
            int y = hits.get(0).getY() * 2 - hits.get(1).getY();

            if (blackList.contains(new Coordinates(x, y)) || x < 0 || y < 0) {
                int size = hits.size();
                x = hits.get(size - 1).getX() * 2 - hits.get(size - 2).getX();
                y = hits.get(size - 1).getY() * 2 - hits.get(size - 2).getY();
            }

            shot = new Coordinates(x, y);
        }

        return shot;
    }
}
