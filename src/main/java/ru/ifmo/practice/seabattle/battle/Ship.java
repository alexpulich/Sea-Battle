package ru.ifmo.practice.seabattle.battle;

import java.util.HashSet;

class Ship {
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
