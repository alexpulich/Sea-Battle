package ru.ifmo.practice.seabattle.server.battleservers;

import ru.ifmo.practice.seabattle.battle.Coordinates;

import java.util.HashSet;

public class ShipMovement {

    private HashSet<Coordinates> newPlace;
    private HashSet<Coordinates> oldPlace;

    public HashSet<Coordinates> getNewPlace() {
        return newPlace;
    }

    public HashSet<Coordinates> getOldPlace() {
        return oldPlace;
    }

    public ShipMovement(HashSet<Coordinates> newPlace, HashSet<Coordinates> oldPlace) {
        this.newPlace = newPlace;
        this.oldPlace = oldPlace;
    }
}
