package ru.ifmo.practice.seabattle.battle;

import ru.ifmo.practice.seabattle.exceptions.IllegalNumberOfShipException;

import java.util.ArrayList;
import java.util.HashSet;

public class Field {
    private ArrayList<Ship> ships = new ArrayList<>();
    private int numberOfNotDestroyedDecks = 0;
    private HashSet<Coordinates> shots = new HashSet<>();

    Field(ArrayList<Ship> ships) {
        this.ships = ships;
    }

    public int getNumberOfNotDestroyedDecks() {
        return numberOfNotDestroyedDecks;
    }

    // в случае промаха возвращает null
    public HashSet<Coordinates> shot(Coordinates shot) throws IllegalNumberOfShipException {
        if (ships.size() != 10) throw new IllegalNumberOfShipException();
        if (shots.contains(shot)) throw new IllegalArgumentException("В эту клетку уже стреляли");

        shots.add(shot);
        HashSet<Coordinates> result = new HashSet<>();

        for (Ship ship : ships) {
            if (ship.getDecks().contains(shot)) {
                result.addAll(ship.shot(shot));
                break;
            }
        }

        if (result.isEmpty()) return null;
        else {
            numberOfNotDestroyedDecks++;
            return result;
        }
    }
}
