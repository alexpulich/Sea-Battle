package ru.ifmo.practice.seabattle.battle;

public class Battle {
    private Gamer firstGamer;
    private Gamer secondGamer;
    private Gamer winner;
    private Gamer looser;

    public Gamer getWinner() {
        return winner;
    }

    public Gamer getLooser() {
        return looser;
    }

    public Battle(Gamer firstGamer, Gamer secondGamer) {
        this.firstGamer = firstGamer;
        this.secondGamer = secondGamer;
        winner = null;
        looser = null;
    }

    public void startBattle() {
        Gamer currentAttacker = firstGamer;
        Gamer currentDefender = secondGamer;

        while (winner == null && looser == null) {
            Coordinates coordinatesOfSootsCell = currentAttacker.nextRound();
            Cell shotCell = currentDefender.getCurrentField().getCell(coordinatesOfSootsCell);

            if (!shotCell.shotInCell()) continue;

            if (currentDefender.getCurrentField().getNumberOfCellsWithShip() == 0) {
                winner = currentAttacker;
                looser = currentDefender;
            }

            if (shotCell.getCellStatus() == CellStatus.Miss) {
                Gamer temp = currentAttacker;
                currentAttacker = currentDefender;
                currentDefender = temp;
            }
        }
    }
}
