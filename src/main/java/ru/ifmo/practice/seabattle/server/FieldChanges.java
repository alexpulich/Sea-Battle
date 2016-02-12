package ru.ifmo.practice.seabattle.server;

import ru.ifmo.practice.seabattle.battle.Coordinates;

class FieldChanges {
    private FieldStatus fieldStatus;
    private Coordinates hit;
    private Coordinates[] misses;

    public FieldChanges(FieldStatus fieldStatus, Coordinates hit, Coordinates[] misses) {
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

    public Coordinates[] getMisses() {
        return misses;
    }
}
