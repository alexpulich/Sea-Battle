package ru.ifmo.practice.seabattle.battle;

import java.util.ArrayList;

public class Cell {
    private CellStatus cellStatus;
    private ArrayList<ShotListner> listners = new ArrayList<>();

    public CellStatus getCellStatus() {
        return cellStatus;
    }

    public static Cell getVoidCell() {
        return new Cell(CellStatus.Void);
    }

    public static Cell getShipCell() {
        return new Cell(CellStatus.Ship);
    }

    private Cell(CellStatus cellStatus) {
        this.cellStatus = cellStatus;
    }

    boolean shotInCell() {
        if (cellStatus == CellStatus.Void) {
            fireListners(cellStatus);
            cellStatus = CellStatus.Miss;
        }
        else if (cellStatus == CellStatus.Ship) {
            fireListners(cellStatus);
            cellStatus = CellStatus.Hit;
        }
        else return false;

        return true;
    }

    public void addListner(ShotListner listner) {
        listners.add(listner);
    }

    public void removeListner(ShotListner listner){
        listners.remove(listner);
    }

    private void fireListners(CellStatus status) {
        listners.forEach((listner) -> listner.onShoted(status));
    }
}
