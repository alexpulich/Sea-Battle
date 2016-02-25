package ru.ifmo.practice.seabattle.server.battleservers;

import ru.ifmo.practice.seabattle.battle.Coordinates;

import java.util.HashSet;

class ShipMovement {
    private HashSet<Coordinates> oldPlace;
    private HashSet<Coordinates> newPlace;

    public HashSet<Coordinates> getOldPlace() {
        return oldPlace;
    }

    public HashSet<Coordinates> getNewPlace() {
        return newPlace;
    }

    public ShipMovement(HashSet<Coordinates> oldPlace, HashSet<Coordinates> newPlace) {
        this.oldPlace = oldPlace;
        this.newPlace = newPlace;
    }
}
