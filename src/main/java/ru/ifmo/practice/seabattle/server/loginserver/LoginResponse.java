package ru.ifmo.practice.seabattle.server.loginserver;

public class LoginResponse {
    private boolean login;
    private boolean serverOk;

    public LoginResponse(boolean login, boolean serverOk) {
        this.login = login;
        this.serverOk = serverOk;
    }
}
