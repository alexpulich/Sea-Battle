package ru.ifmo.practice.seabattle.battle;

import java.io.IOException;
import java.util.HashSet;

public interface ShotListener {
    void shot(Field field, Coordinates hit, HashSet<Coordinates> misses) throws IOException;
}
