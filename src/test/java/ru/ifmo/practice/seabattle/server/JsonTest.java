package ru.ifmo.practice.seabattle.server;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import ru.ifmo.practice.seabattle.Console;
import ru.ifmo.practice.seabattle.battle.*;

import java.util.HashSet;

public class JsonTest implements FieldChangesListener {
    public static void main(String[] args) {
        FirstFieldBuilder firstFieldBuilder = new FirstFieldBuilder();
        HashSet<Coordinates> ship = new HashSet<>();

        ship.add(new Coordinates(1, 0));
        ship.add(new Coordinates(1, 1));
        ship.add(new Coordinates(1, 2));
        firstFieldBuilder.addShip(ship);
        ship = new HashSet<>();

        ship.add(new Coordinates(0, 5));
        firstFieldBuilder.addShip(ship);
        ship = new HashSet<>();

        ship.add(new Coordinates(0, 8));
        ship.add(new Coordinates(1, 8));
        firstFieldBuilder.addShip(ship);
        ship = new HashSet<>();

        ship.add(new Coordinates(4, 3));
        ship.add(new Coordinates(4, 4));
        ship.add(new Coordinates(4, 5));
        ship.add(new Coordinates(5, 6));
        firstFieldBuilder.addShip(ship);
        ship = new HashSet<>();

        ship.add(new Coordinates(4, 8));
        firstFieldBuilder.addShip(ship);
        ship = new HashSet<>();

        ship.add(new Coordinates(5, 1));
        firstFieldBuilder.addShip(ship);
        ship = new HashSet<>();

        ship.add(new Coordinates(6, 8));
        ship.add(new Coordinates(7, 8));
        firstFieldBuilder.addShip(ship);
        ship = new HashSet<>();

        ship.add(new Coordinates(7, 5));
        ship.add(new Coordinates(8, 5));
        ship.add(new Coordinates(9, 5));
        firstFieldBuilder.addShip(ship);
        ship = new HashSet<>();

        ship.add(new Coordinates(8, 2));
        ship.add(new Coordinates(9, 2));
        firstFieldBuilder.addShip(ship);
        ship = new HashSet<>();

        ship.add(new Coordinates(9, 7));
        firstFieldBuilder.addShip(ship);

        FirstField field = firstFieldBuilder.create();

        Gson gson = new GsonBuilder().serializeNulls().create();

        Console.outputMessage(gson.toJson(field.getCurrentConditions()));
        Console.outputMessage(gson.toJson(new Message<>(field.getCurrentConditions())));

        field.addChangesListener(new JsonTest());
        field.shot(new Coordinates(0, 5));
        field.shot(new Coordinates(8, 5));
        field.shot(new Coordinates(0, 9));

        Console.outputMessage(gson.toJson(new Message<>(Notice.YourTurn)));
    }

    @Override
    public void fieldChanged(Field field, Coordinates hit, HashSet<Coordinates> misses) {
        GsonBuilder gsonBuilder = new GsonBuilder();
        Gson gson = gsonBuilder.serializeNulls().create();

        FieldChanges changes = new FieldChanges(FieldStatus.Second, hit, misses);

        Console.outputMessage(gson.toJson(new Message<>(changes)));
    }
}
