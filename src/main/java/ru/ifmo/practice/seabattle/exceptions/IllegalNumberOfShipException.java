package ru.ifmo.practice.seabattle.exceptions;

public class IllegalNumberOfShipException extends Exception {
    public IllegalNumberOfShipException() {}
    public IllegalNumberOfShipException(String message) {
        super(message);
    }
}
