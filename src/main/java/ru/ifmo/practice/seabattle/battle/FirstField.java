package ru.ifmo.practice.seabattle.battle;

import ru.ifmo.practice.seabattle.exceptions.IllegalNumberOfShipException;

import java.util.ArrayList;
import java.util.HashSet;

public class FirstField implements Field {
    private ArrayList<Ship> ships = new ArrayList<>();
    private int numberOfDestroyedDecks = 0;
    private HashSet<Coordinates> shots = new HashSet<>();
    private HashSet<FieldChangesListener> listeners = new HashSet<>();

    FirstField(ArrayList<Ship> ships) {
        this.ships = ships;
    }

    @Override
    public Cell[][] getCurrentConditions() {
        Cell[][] result = new Cell[10][10];

        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                Coordinates coordinates = new Coordinates(i, j);
                Cell cell = Cell.Void;
                if (shots.contains(coordinates)) cell = Cell.Miss;

                for (Ship ship : ships) {
                    if (ship.getOccupiedSpace().contains(coordinates)) {
                        if (ship.getDecks().contains(coordinates)) {
                            cell = Cell.Ship;
                            break;
                        } else if (ship.getDestroyedDecks().contains(coordinates)) {
                            cell = Cell.Hit;
                            break;
                        } else if (ship.getSpaceAround().contains(coordinates)) break;
                    }
                }

                result[i][j] = cell;
            }
        }

        return result;
    }

    public ArrayList<HashSet<Coordinates>> getCurrentShips() {
        ArrayList<HashSet<Coordinates>> result = new ArrayList<>();

        ships.forEach((ship) -> {
            HashSet<Coordinates> decks = new HashSet<>();
            decks.addAll(ship.getDecks());
            decks.addAll(ship.getDestroyedDecks());

            result.add(decks);
        });

        return result;
    }

    public int getNumberOfDestroyedDecks() {
        return numberOfDestroyedDecks;
    }

    // в случае промаха возвращает null
    public HashSet<Coordinates> shot(Coordinates shot) throws IllegalNumberOfShipException {
        if (ships.size() != 10) throw new IllegalNumberOfShipException();
        if (shots.contains(shot)) throw new IllegalArgumentException("В эту клетку уже стреляли");

        shots.add(shot);
        HashSet<Coordinates> result = new HashSet<>();

        ships.forEach((ship) -> {
            if (ship.getDecks().contains(shot))
                result.addAll(ship.shot(shot));
        });

        if (result.isEmpty()) {
            HashSet<Coordinates> misses = new HashSet<>();
            misses.add(shot);
            fireListeners(null, misses);

            return null;
        }
        else {
            HashSet<Coordinates> misses = new HashSet<>();
            misses.addAll(result);
            misses.remove(shot);
            if (misses.isEmpty()) misses = null;
            fireListeners(shot, misses);

            numberOfDestroyedDecks++;
            return result;
        }
    }

    @Override
    public void addChangesListener(FieldChangesListener listener) {
        listeners.add(listener);
    }

    @Override
    public boolean removeChangesListener(FieldChangesListener listener) {
        return listeners.remove(listener);
    }

    private void fireListeners(Coordinates hit, HashSet<Coordinates> misses) {
        listeners.forEach(listener -> listener.fieldChanged(this, hit, misses));
    }

    @Override
    public String toString() {
        Cell[][] currentField = getCurrentConditions();
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++)
                result.append(currentField[i][j]);
            result.append("\n");
        }

        return result.toString();
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof FirstField && this.toString().equals(toString());
    }
}
