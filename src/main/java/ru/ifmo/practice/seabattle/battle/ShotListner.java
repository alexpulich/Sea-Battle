package ru.ifmo.practice.seabattle.battle;

@FunctionalInterface
public interface ShotListner {
    void onShoted(CellStatus status);
}
