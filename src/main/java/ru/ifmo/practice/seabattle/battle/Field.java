package ru.ifmo.practice.seabattle.battle;

public class Field {
    private Cell[][] cells;
    private int numberOfCellsWithShip;

    public Cell[][] getCells() {
        return cells;
    }

    public Cell getCell(int x, int y) {
        return cells[x][y];
    }

    public Cell getCell(Coordinates coordinates) {
        return cells[coordinates.getX()][coordinates.getY()];
    }

    public static Field getVoidField() {
        Cell[][] cells = new Cell[10][10];

        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                cells[i][j] = Cell.getVoidCell();
            }
        }

        return new Field(cells);
    }

    public int getNumberOfCellsWithShip() {
        return numberOfCellsWithShip;
    }

    public Field(Cell[][] cells) {
        if (cells.length != 10) throw new IllegalArgumentException("cells must be Cell[10][10]");
        for (int i = 0; i < 10; i++) {
            if (cells[i].length != 10) throw new IllegalArgumentException("cells must be Cell[10][10]");
        }

        this.cells = cells;
        numberOfCellsWithShip = 20;

        addShotListner((status) -> {
            if (status == CellStatus.Ship) {
                numberOfCellsWithShip--;
            }
        });
    }

    public void addShotListner(ShotListner listner) {
        for (Cell[] cellsArray : cells) {
            for (Cell cell : cellsArray) {
                cell.addListner(listner);
            }
        }
    }
}
