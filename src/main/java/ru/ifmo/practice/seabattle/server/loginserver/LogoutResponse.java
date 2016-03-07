package ru.ifmo.practice.seabattle.server.loginserver;

public class LogoutResponse {
    private boolean logout;
    private boolean wasLogged;

    public LogoutResponse(boolean logout, boolean wasLogged) {
        this.logout = logout;
        this.wasLogged = wasLogged;
    }
}
