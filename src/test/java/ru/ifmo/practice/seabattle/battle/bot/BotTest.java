package ru.ifmo.practice.seabattle.battle.bot;

import ru.ifmo.practice.seabattle.Console;
import ru.ifmo.practice.seabattle.battle.Battle;
import ru.ifmo.practice.seabattle.exceptions.IllegalNumberOfShipException;

public class BotTest {
    public static void main(String[] args) {

        Bot bot1 = new Bot("Бот 1");
        Bot bot2 = new Bot("Бот 2");

        Console.outputField(bot1.getFirstField(), bot1.getNickName());
        Console.outputField(bot2.getFirstField(), bot2.getNickName());

        Battle battle = new Battle(bot1, bot2);
        try {
            battle.start();
        } catch (IllegalNumberOfShipException e) {
            e.printStackTrace();
        }

        Console.outputField(bot1.getFirstField(), bot1.getNickName());
        Console.outputField(bot2.getFirstField(), bot2.getNickName());
    }
}
