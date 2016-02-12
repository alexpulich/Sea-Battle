package ru.ifmo.practice.seabattle.battle;

import ru.ifmo.practice.seabattle.Console;
import ru.ifmo.practice.seabattle.exceptions.IllegalNumberOfShipException;

public class PlaceShipsRandomTest {
    public static void main(String[] args) {
        FieldBuilder fieldBuilder = new FieldBuilder();
        fieldBuilder.placeShipsRandom();
        Field field = null;
        try {
            field = fieldBuilder.create();
        } catch (IllegalNumberOfShipException e) {
            e.printStackTrace();
        }

        Console.outputField(field);
    }
}
