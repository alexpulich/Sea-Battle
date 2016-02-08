package ru.ifmo.practice.seabattle.battle;

import ru.ifmo.practice.seabattle.exceptions.IllegalNumberOfShipException;

import java.util.ArrayList;
import java.util.HashSet;

public class Field {
    private class Ship {
        private HashSet<Coordinates> decks;
        private HashSet<Coordinates> destroyedDecks = new HashSet<>();
        private HashSet<Coordinates> spaceAround = new HashSet<>();

        public HashSet<Coordinates> getDecks() {
            return decks;
        }

        public HashSet<Coordinates> getOccupiedSpace() {
            HashSet<Coordinates> result = new HashSet<>();
            result.addAll(decks);
            result.addAll(destroyedDecks);
            result.addAll(spaceAround);

            return result;
        }

        public Ship(HashSet<Coordinates> decks) {
            if (decks.size() < 1 || decks.size() > 4)
                throw new IllegalArgumentException("Корабль не может содержать " + decks.size() + " палуб");

            this.decks = decks;

            decks.forEach((coord) -> {
                int x = coord.getX();
                int y = coord.getY();

                for (int i = x; i < x + 3; i++) {
                    for (int j = y; j < y + 3; j++) {
                        if (!spaceAround.contains(coord) &&
                                !decks.contains(coord)) spaceAround.add(coord);
                    }
                }
            });
        }

        public HashSet<Coordinates> shot(Coordinates shot) {
            if (destroyedDecks.contains(shot))
                throw new IllegalArgumentException("По данной палубе уже стреляли");
            if (!decks.contains(shot)) throw new IllegalArgumentException("Такой палубы не существует");

            decks.remove(shot);

            HashSet<Coordinates> result = new HashSet<>();
            if (decks.isEmpty()) result.addAll(spaceAround);
            result.add(shot);

            return result;
        }
    }

    private ArrayList<Ship> ships = new ArrayList<>();
    private int numberOfNotDestroyedDecks = 0;
    private int[] numberOfShipsForDecks = new int[5]; // [4] - 4x, [3] - 3x, [2] - 2x, [1] - 1но палубные корабли
    private HashSet<Coordinates> shots = new HashSet<>();

    public int getNumberOfNotDestroyedDecks() {
        return numberOfNotDestroyedDecks;
    }

    public void placeShipsRandom() {
        // ...
    }

    public void addShip(HashSet<Coordinates> shipCoordinates) throws IllegalNumberOfShipException {
        if (ships.size() == 10) throw new IllegalNumberOfShipException("Поле уже заполнено");

        ships.forEach((ship) ->
            shipCoordinates.forEach((coord) -> {
                if (ship.getOccupiedSpace().contains(coord))
                    throw new IllegalArgumentException("Место уже занято");
            })
        );

        if (numberOfShipsForDecks[shipCoordinates.size()] ==
                5 - shipCoordinates.size())
            throw new IllegalArgumentException("Количество " +
                    shipCoordinates.size() + " палубных кораблей превышено");

        ships.add(new Ship(shipCoordinates));
        numberOfShipsForDecks[shipCoordinates.size()]++;
    }

    // в случае промаха возвращает пустую коллекцию
    public HashSet<Coordinates> shot(Coordinates shot) throws IllegalNumberOfShipException {
        if (ships.size() != 10) throw new IllegalNumberOfShipException();
        if (shots.contains(shot)) throw new IllegalArgumentException("В эту клетку уже стреляли");

        shots.add(shot);
        HashSet<Coordinates> result = new HashSet<>();

        ships.forEach((ship) -> {
            if (ship.getDecks().contains(shot))
                result.addAll(ship.shot(shot));
        });

        return result;
    }
}
