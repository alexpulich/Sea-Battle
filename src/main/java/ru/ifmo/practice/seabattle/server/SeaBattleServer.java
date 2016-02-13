package ru.ifmo.practice.seabattle.server;

import ru.ifmo.practice.seabattle.battle.BattleEndedListener;
import ru.ifmo.practice.seabattle.battle.ShotListener;

import java.io.IOException;

public interface SeaBattleServer extends ShotListener, BattleEndedListener {
    void placeShipsRandom(String sessionID) throws IOException;
    void setField(String message, String sessionId) throws IOException;
    void startBattle(String sessionId) throws IOException;
    void shot(String message, String sessionId) throws IOException;
    void sendMessage(String message, String sessionId) throws IOException;
}
