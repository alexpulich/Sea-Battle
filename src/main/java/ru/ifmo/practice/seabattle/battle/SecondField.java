package ru.ifmo.practice.seabattle.battle;

import java.util.*;

public class SecondField implements Field {
    private Cell[][] field;
    private HashSet<FieldChangesListener> listeners = new HashSet<>();

    public SecondField() {
        field = new Cell[10][10];
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                field[i][j] = Cell.Void;
            }
        }
    }

    public void change(HashMap<Coordinates, Cell> changes) {
        Coordinates hit = null;
        HashSet<Coordinates> misses = new HashSet<>();
        Cell[][] cells = new Cell[10][10];

        for (int i = 0; i < 10; i++) {
            System.arraycopy(field[i], 0, cells[i], 0, 10);
        }

        Set<Map.Entry<Coordinates, Cell>> entrySet = changes.entrySet();
        for (Map.Entry<Coordinates, Cell> entry : entrySet) {
            if (entry.getKey().getX() > 9 || entry.getKey().getX() < 0
                    || entry.getKey().getY() > 9 || entry.getKey().getX() < 0)
                throw new IllegalArgumentException();

            if (entry.getValue() == Cell.Hit && hit == null) hit = entry.getKey();
            else if (entry.getValue() == Cell.Miss) misses.add(entry.getKey());
            else throw new IllegalArgumentException();
        }

        field = cells;

        if (misses.size() == 0) misses = null;
        fireChangesListeners(hit, misses);
    }

    @Override
    public Cell[][] getCurrentConditions() {
        return field;
    }

    @Override
    public void addChangesListener(FieldChangesListener listener) {
        listeners.add(listener);
    }

    @Override
    public boolean removeChangesListener(FieldChangesListener listener) {
        return listeners.remove(listener);
    }

    private void fireChangesListeners(Coordinates hit, HashSet<Coordinates> misses) {
        listeners.forEach(listener -> listener.fieldChanged(this, hit, misses));
    }
}
