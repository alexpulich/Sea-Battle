package ru.ifmo.practice.seabattle.exceptions;

public class IllegalNumberOfShipException extends RuntimeException {
    public IllegalNumberOfShipException() {}
    public IllegalNumberOfShipException(String message) {
        super(message);
    }
}
