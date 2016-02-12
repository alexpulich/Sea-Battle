package ru.ifmo.practice.seabattle.server;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import ru.ifmo.practice.seabattle.Console;
import ru.ifmo.practice.seabattle.battle.*;

import java.util.HashSet;

public class JsonTest implements ShotListener {
    public static void main(String[] args) {
        GsonBuilder gsonBuilder = new GsonBuilder();
        Gson gson = gsonBuilder.setPrettyPrinting().create();

        FieldBuilder builder = new FieldBuilder();
        builder.placeShipsRandom();
        Field field = builder.create();

        Console.outputField(field);

        Cell[][] cells = field.getCurrentConditions();
        String message = gson.toJson(cells);

        Console.outputMessage(message);

        cells = gson.fromJson(message, Cell[][].class);
        builder.clear();
        builder.addShips(cells);
        field = builder.create();

        Console.outputField(field);

        builder.clear();
        HashSet<Coordinates> ship = new HashSet<>();

        ship.add(new Coordinates(1, 0));
        ship.add(new Coordinates(1, 1));
        ship.add(new Coordinates(1, 2));
        builder.addShip(ship);
        ship = new HashSet<>();

        ship.add(new Coordinates(0, 5));
        builder.addShip(ship);
        ship = new HashSet<>();

        ship.add(new Coordinates(0, 8));
        ship.add(new Coordinates(1, 8));
        builder.addShip(ship);
        ship = new HashSet<>();

        ship.add(new Coordinates(4, 3));
        ship.add(new Coordinates(4, 4));
        ship.add(new Coordinates(4, 5));
        ship.add(new Coordinates(5, 6));
        builder.addShip(ship);
        ship = new HashSet<>();

        ship.add(new Coordinates(4, 8));
        builder.addShip(ship);
        ship = new HashSet<>();

        ship.add(new Coordinates(5, 1));
        builder.addShip(ship);
        ship = new HashSet<>();

        ship.add(new Coordinates(6, 8));
        ship.add(new Coordinates(7, 8));
        builder.addShip(ship);
        ship = new HashSet<>();

        ship.add(new Coordinates(7, 5));
        ship.add(new Coordinates(8, 5));
        ship.add(new Coordinates(9, 5));
        builder.addShip(ship);
        ship = new HashSet<>();

        ship.add(new Coordinates(8, 2));
        ship.add(new Coordinates(9, 2));
        builder.addShip(ship);
        ship = new HashSet<>();

        ship.add(new Coordinates(9, 7));
        builder.addShip(ship);

        field = builder.create();
        field.addShotListener(new JsonTest());
        Coordinates shot = new Coordinates(0, 5);
        field.shot(shot);
    }

    @Override
    public void shot(Field field, Coordinates hit, HashSet<Coordinates> misses) {
        GsonBuilder gsonBuilder = new GsonBuilder();
        Gson gson = gsonBuilder.setPrettyPrinting().create();

        FieldChanges changes = new FieldChanges(FieldStatus.Second, hit,
                misses.toArray(new Coordinates[misses.size()]));

        Console.outputMessage(gson.toJson(changes));
    }
}
