package ru.ifmo.practice.seabattle.exceptions;

public class BattleNotFinishedException extends RuntimeException {
    public BattleNotFinishedException() {}
    public BattleNotFinishedException(String message) {
        super(message);
    }
}
