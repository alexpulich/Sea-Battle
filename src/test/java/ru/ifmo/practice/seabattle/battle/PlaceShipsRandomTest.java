package ru.ifmo.practice.seabattle.battle;

import ru.ifmo.practice.seabattle.Console;
import ru.ifmo.practice.seabattle.exceptions.IllegalNumberOfShipException;

public class PlaceShipsRandomTest {
    public static void main(String[] args) {
        FirstFieldBuilder firstFieldBuilder = new FirstFieldBuilder();
        firstFieldBuilder.placeShipsRandom();
        FirstField field = null;
        try {
            field = firstFieldBuilder.create();
        } catch (IllegalNumberOfShipException e) {
            e.printStackTrace();
        }

        Console.outputField(field);
    }
}
