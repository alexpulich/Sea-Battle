package ru.ifmo.practice.seabattle.battle;

import ru.ifmo.practice.seabattle.exceptions.IllegalNumberOfShipException;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;

public class Field {
    private class Ship {
        private HashSet<Coordinates> decks = new HashSet<>();
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

        // Возвращает корабль размера size расположенный случайным образом
        public Ship(int size) {
            Random random = new Random();

            Route route;
            int x = random.nextInt(10), y;

            if (10 - x < size) {
                y = random.nextInt(10 - size + 1);
                route = Route.Horizontal;
            } else {
                y = random.nextInt(10);
                if (10 - y < size) route = Route.Vertical;
                else route = Route.getRoute(random.nextInt(2));
            }

            Coordinates startingPoint = new Coordinates(x, y);

            if (route == Route.Horizontal) {
                for (int i = 0; i < size; i++) {
                    decks.add(new Coordinates(startingPoint.getX(), startingPoint.getY() + i));
                }
            } else {
                for (int i = 0; i < size; i++) {
                    decks.add(new Coordinates(startingPoint.getX() + i, startingPoint.getY()));
                }
            }

            setSpaceAround();
        }

        public Ship(HashSet<Coordinates> decks) {
            if (decks.size() < 1 || decks.size() > 4)
                throw new IllegalArgumentException("Корабль не может содержать " + decks.size() + " палуб");

            this.decks = decks;
            setSpaceAround();
        }

        private void setSpaceAround() {
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
            destroyedDecks.add(shot);

            HashSet<Coordinates> result = new HashSet<>();
            if (decks.isEmpty()) result.addAll(spaceAround);
            result.add(shot);

            return result;
        }
    }

    private enum Route {
        Vertical, Horizontal;

        public static Route getRoute(int n) throws IllegalArgumentException {
            if (n < 0 || n > 1) throw new IllegalArgumentException();
            else if (n == 0) return Vertical;
            else return Horizontal;
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
        for (int i = 1; i <= 4; i++) {
            int n = 5 - i;  // n - Количество клеток в корабле
                            // i - Количество n-клеточных кораблей

            for (int j = 0; j < i; j++) {
                Ship ship;
                do {
                    ship = new Ship(n);
                } while (isPlaceOccuped(ship));

                ships.add(ship);
            }
        }
    }

    private boolean isPlaceOccuped(Ship ship) {
        return isPlaceOccuped(ship.getDecks());
    }

    private boolean isPlaceOccuped(HashSet<Coordinates> shipCoordinates) {
        for (Ship ship : ships)
            for (Coordinates coordinates : shipCoordinates)
                if (ship.getOccupiedSpace().contains(coordinates))
                    return true;

        return false;
    }

    public void addShip(HashSet<Coordinates> shipCoordinates) throws IllegalNumberOfShipException {
        if (ships.size() == 10) throw new IllegalNumberOfShipException("Поле уже заполнено");

        if (isPlaceOccuped(shipCoordinates)) {
            throw new IllegalArgumentException("Место уже занято");
        }

        if (numberOfShipsForDecks[shipCoordinates.size()] ==
                5 - shipCoordinates.size())
            throw new IllegalArgumentException("Количество " +
                    shipCoordinates.size() + " палубных кораблей превышено");

        ships.add(new Ship(shipCoordinates));
        numberOfShipsForDecks[shipCoordinates.size()]++;
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
            numberOfNotDestroyedDecks++;
            return result;
        }
    }
}
