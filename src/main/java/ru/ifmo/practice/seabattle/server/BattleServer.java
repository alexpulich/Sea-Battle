package ru.ifmo.practice.seabattle.server;

import ru.ifmo.practice.seabattle.battle.BattleEndedListener;
import ru.ifmo.practice.seabattle.battle.NextTurnListener;
import ru.ifmo.practice.seabattle.battle.ShotInFieldListener;

import java.io.IOException;

interface BattleServer extends ShotInFieldListener, BattleEndedListener, NextTurnListener {
    void placeShipsRandom(String sessionID) throws IOException;
    void setField(String message, String sessionId) throws IOException;
    void startBattle(String sessionId) throws IOException;
    void shot(String message, String sessionId) throws IOException;
    void sendMessage(Message message, String sessionId) throws IOException;
}
