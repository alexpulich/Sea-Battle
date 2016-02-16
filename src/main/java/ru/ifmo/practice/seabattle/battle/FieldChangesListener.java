package ru.ifmo.practice.seabattle.battle;

import java.util.HashSet;

public interface FieldChangesListener {
    void fieldChanged(Field field, Coordinates hit, HashSet<Coordinates> misses);
}
