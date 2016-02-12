package ru.ifmo.practice.seabattle.server;

import ru.ifmo.practice.seabattle.Console;
import ru.ifmo.practice.seabattle.battle.Field;
import ru.ifmo.practice.seabattle.battle.FieldBuilder;
import ru.ifmo.practice.seabattle.battle.Gamer;

import java.util.HashMap;

public class CollectionTest {
    public static void main(String[] args) {
        FieldBuilder builder = new FieldBuilder();
        builder.placeShipsRandom();
        Field field = builder.create();
        Player player = new Player("player", field);

        HashMap<String, Player> players = new HashMap<>();
        players.put("player", player);

        Gamer gamer = new Player("player", field);
        Console.outputMessage(players.containsValue(gamer) + "");

        gamer = player;
        Console.outputMessage(players.containsValue(gamer) + "");
    }
}
