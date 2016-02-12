package ru.ifmo.practice.seabattle.battle;

import ru.ifmo.practice.seabattle.exceptions.IllegalNumberOfShipException;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;

public class FieldBuilder {
    private ArrayList<Ship> ships = new ArrayList<>();
    private int[] numberOfShipsForDecks = new int[5]; // [4] - 4x, [3] - 3x, [2] - 2x, [1] - 1но палубные корабли

    public Field create() throws IllegalNumberOfShipException {
        if (ships.size() < 10) throw new IllegalNumberOfShipException("Поле еще не заполнено");
        return new Field(ships);
    }

    public void addShip(HashSet<Coordinates> shipCoordinates) throws IllegalNumberOfShipException {
        if (ships.size() == 10) throw new IllegalNumberOfShipException("Поле уже заполнено");

        shipCoordinates.forEach((deck) -> {
            if (deck.getX() > 9 || deck.getX() < 0
                    || deck.getY() > 9 || deck.getY() < 0)
                throw new IllegalArgumentException("Корабль не помещается на поле");
        });

        ships.forEach((ship) ->
                shipCoordinates.forEach((coord) -> {
                    if (ship.getOccupiedSpace().contains(coord))
                        throw new IllegalArgumentException("Место уже занято");
                })
        );

        if (numberOfShipsForDecks[shipCoordinates.size()] ==
                5 - shipCoordinates.size())
            throw new IllegalNumberOfShipException("Количество " +
                    shipCoordinates.size() + " палубных кораблей превышено");

        ships.add(new Ship(shipCoordinates));
        numberOfShipsForDecks[shipCoordinates.size()]++;
    }

    public boolean removeShip(HashSet<Coordinates> shipCoordinates) {
        Iterator<Ship> iterator = ships.iterator();
        while (iterator.hasNext()) {
            Ship ship = iterator.next();
            if (ship.getDecks().containsAll(shipCoordinates)) {
                iterator.remove();
                numberOfShipsForDecks[shipCoordinates.size()]--;
                return true;
            }
        }

        return false;
    }

    public void clear() {
        ships.clear();
        for (int i = 0; i < numberOfShipsForDecks.length; i++) {
            numberOfShipsForDecks[i] = 0;
        }
    }

    public void addShips(Cell[][] field) throws IllegalNumberOfShipException {
        if (field.length != 10) throw new IllegalArgumentException("Поле должно быть 10x10");

        HashSet<Coordinates> blacklist = new HashSet<>();
        HashSet<HashSet<Coordinates>> ships = new HashSet<>();

        for (int i = 0; i < field.length; i++) {
            int n = field[i].length;
            if (n != 10) throw new IllegalArgumentException("Поле должно быть 10x10");
            for (int j = 0; j < n; j++) {
                if (!blacklist.contains(new Coordinates(i, j))) {
                    if (field[i][j] == Cell.Ship) {
                        HashSet<Coordinates> ship = new HashSet<>();
                        ship.add(new Coordinates(i, j));

                        int k = 1;

                        if (j + k < n && field[i][j + k] == Cell.Ship) {
                            ship.add(new Coordinates(i, j + k));
                            k++;

                            while (j + k < n) {
                                if (field[i][j + k] == Cell.Ship) ship.add(new Coordinates(i, j + k));
                                else break;
                            }
                        } else if (i + k < n && field[i + k][j] == Cell.Ship) {
                            ship.add(new Coordinates(i + k, j));
                            k++;

                            while (i + k < n) {
                                if (field[i + k][j] == Cell.Ship) ship.add(new Coordinates(i + k, j));
                                else break;
                            }
                        }

                        blacklist.addAll(ship);
                        ships.add(ship);
                    } else if (field[i][j] != Cell.Void) {
                        throw new IllegalArgumentException("Поле должо содержать только пустые клетки и корабли");
                    }
                }
            }
        }

        for (HashSet<Coordinates> ship : ships) addShip(ship);
    }
}
