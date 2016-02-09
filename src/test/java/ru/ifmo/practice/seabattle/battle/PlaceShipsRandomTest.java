package ru.ifmo.practice.seabattle.battle;

import ru.ifmo.practice.seabattle.Console;

public class PlaceShipsRandomTest {
    public static void main(String[] args) {
        Field field = new Field();
        field.placeShipsRandom();

        Console.outputField(field);
    }
}
