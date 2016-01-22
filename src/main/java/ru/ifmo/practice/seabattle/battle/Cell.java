package ru.ifmo.practice.seabattle.battle;

public class Cell {
    private int x;
    private int y;
    private CellStatus cellStatus;

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public CellStatus getCellStatus() {
        return cellStatus;
    }

    public static Cell getVoidCell(int x, int y) {
        return new Cell(x, y, CellStatus.Void);
    }

    public static Cell getShipCell(int x, int y) {
        return new Cell(x, y, CellStatus.Ship);
    }

    private Cell(int x, int y, CellStatus cellStatus) {
        this.x = x;
        this.y = y;
        this.cellStatus = cellStatus;
    }

    boolean shotInCell() {
        if (cellStatus == CellStatus.Void) cellStatus = CellStatus.Miss;
        else if (cellStatus == CellStatus.Ship) cellStatus = CellStatus.Hit;
        else return false;

        return true;
    }
}
