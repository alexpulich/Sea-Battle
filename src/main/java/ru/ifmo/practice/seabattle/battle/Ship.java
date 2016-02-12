package ru.ifmo.practice.seabattle.battle;

import java.util.HashSet;
import java.util.Random;

class Ship {
    private enum Route {
        Vertical, Horizontal;

        public static Route getRoute(int n) throws IllegalArgumentException {
            if (n < 0 || n > 1) throw new IllegalArgumentException();
            else if (n == 0) return Vertical;
            else return Horizontal;
        }
    }

    private HashSet<Coordinates> decks = new HashSet<>();
    private HashSet<Coordinates> destroyedDecks = new HashSet<>();
    private HashSet<Coordinates> spaceAround = new HashSet<>();

    public HashSet<Coordinates> getDecks() {
        return decks;
    }

    public HashSet<Coordinates> getDestroyedDecks() {
        return destroyedDecks;
    }

    public HashSet<Coordinates> getSpaceAround() {
        return spaceAround;
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
        decks.forEach((deck) -> {
            int x = deck.getX();
            int y = deck.getY();

            for (int i = x - 1; i < x + 2; i++) {
                for (int j = y - 1; j < y + 2; j++) {
                    Coordinates coord = new Coordinates(i, j);
                    if (!spaceAround.contains(coord) && !decks.contains(coord)
                            && i >= 0 && i <= 9 && j >=0 && j <=9 )
                        spaceAround.add(coord);
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


