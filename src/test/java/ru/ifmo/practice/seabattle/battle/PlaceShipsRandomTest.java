package ru.ifmo.practice.seabattle.battle;

public class PlaceShipsRandomTest {
    public static void main(String[] args) {
        Field field = new Field();
        field.placeShipsRandom();

        Cell[][] fieldCells = field.getCurrentConditions();

        for (Cell[] cells : fieldCells) {
            for (Cell cell : cells) {
                char symbol = 'o';

                switch (cell) {
                    case Void:
                        symbol = 'v';
                        break;

                    case Ship:
                        symbol = 's';
                        break;

                    case Miss:
                        symbol = 'm';
                        break;

                    case Hit:
                        symbol = 'h';
                        break;
                }

                System.out.print(symbol);
            }

            System.out.print("\n");
        }
    }
}
