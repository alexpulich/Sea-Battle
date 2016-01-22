package ru.ifmo.practice.seabattle.battle;

public class Field {
    private Cell[][] cells;

    public Cell[][] getCells() {
        return cells;
    }

    public Cell getCell(int x, int y) {
        return cells[x][y];
    }

    public Field(Cell[][] cells) {
        if (cells.length != 10) throw new IllegalArgumentException("cells must be Cell[10][10]");
        for (int i = 0; i < 10; i++) {
            if (cells[i].length != 10) throw new IllegalArgumentException("cells must be Cell[10][10]");
        }
        this.cells = cells;
    }
}
