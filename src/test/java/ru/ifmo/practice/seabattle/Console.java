package ru.ifmo.practice.seabattle;

import ru.ifmo.practice.seabattle.battle.Cell;
import ru.ifmo.practice.seabattle.battle.Field;

public class Console {
    public static void outputField(Field field) {
        System.out.print("\n");

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

        System.out.print("\n");
    }

    public static void outputField(Field field, String nickname) {
        System.out.print("\n" + nickname);
        outputField(field);
    }

    public static void outputMessage(String message) {
        System.out.print("\n");
        System.out.print(message + "\n");
        System.out.print("\n");
    }
}
