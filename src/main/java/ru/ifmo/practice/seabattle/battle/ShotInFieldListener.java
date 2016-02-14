package ru.ifmo.practice.seabattle.battle;

import java.util.HashSet;

public interface ShotInFieldListener {
    void shotInField(Field field, Coordinates hit, HashSet<Coordinates> misses);
}
