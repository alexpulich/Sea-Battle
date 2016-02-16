package ru.ifmo.practice.seabattle.battle;

public interface Field {
    Cell[][] getCurrentConditions();
    void addChangesListener(FieldChangesListener listener);
    boolean removeChangesListener(FieldChangesListener listener);
}
