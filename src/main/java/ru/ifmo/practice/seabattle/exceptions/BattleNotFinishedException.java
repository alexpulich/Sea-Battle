package ru.ifmo.practice.seabattle.exceptions;

public class BattleNotFinishedException extends Exception {
    public BattleNotFinishedException() {}
    public BattleNotFinishedException(String message) {
        super(message);
    }
}
