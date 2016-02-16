package ru.ifmo.practice.seabattle.server;

import ru.ifmo.practice.seabattle.Console;
import ru.ifmo.practice.seabattle.battle.FirstField;
import ru.ifmo.practice.seabattle.battle.FirstFieldBuilder;
import ru.ifmo.practice.seabattle.battle.Gamer;
import ru.ifmo.practice.seabattle.battle.SecondField;

import java.util.HashMap;

public class CollectionTest {
    public static void main(String[] args) {
        FirstFieldBuilder builder = new FirstFieldBuilder();
        builder.placeShipsRandom();
        FirstField field = builder.create();
        Player player = new Player("player", field, new SecondField());

        HashMap<String, Player> players = new HashMap<>();
        players.put("player", player);

        Gamer gamer = new Player("player", field, new SecondField());
        Console.outputMessage(players.containsValue(gamer) + "");

        gamer = player;
        Console.outputMessage(players.containsValue(gamer) + "");
    }
}
