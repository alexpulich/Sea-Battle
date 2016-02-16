package ru.ifmo.practice.seabattle.server;

import ru.ifmo.practice.seabattle.battle.Coordinates;

import java.util.HashSet;

class FieldChanges {
    private FieldStatus fieldStatus;
    private Coordinates hit;
    private HashSet<Coordinates> misses;

    public FieldChanges(FieldStatus fieldStatus, Coordinates hit, HashSet<Coordinates> misses) {
        this.fieldStatus = fieldStatus;
        this.hit = hit;
        this.misses = misses;
    }

    public FieldStatus getFieldStatus() {
        return fieldStatus;
    }

    public Coordinates getHit() {
        return hit;
    }

    public HashSet<Coordinates> getMisses() {
        return misses;
    }
}
