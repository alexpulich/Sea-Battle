package ru.ifmo.practice.seabattle.battle;

import ru.ifmo.practice.seabattle.exceptions.IllegalNumberOfShipException;

import java.util.ArrayList;
import java.util.HashSet;

public class Field {
    private ArrayList<Ship> ships = new ArrayList<>();
    private int numberOfDestroyedDecks = 0;
    private HashSet<Coordinates> shots = new HashSet<>();

    Field(ArrayList<Ship> ships) {
        this.ships = ships;
    }

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

        if (result.isEmpty()) return null;
        else {
            numberOfDestroyedDecks++;
            return result;
        }
    }
}
