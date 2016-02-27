package ru.ifmo.practice.seabattle.server;

public class LoginResponse {
    private boolean login;
    private boolean serverOk;

    public LoginResponse(boolean login, boolean serverOk) {
        this.login = login;
        this.serverOk = serverOk;
    }
}
