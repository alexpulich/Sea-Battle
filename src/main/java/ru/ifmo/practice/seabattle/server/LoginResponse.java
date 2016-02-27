package ru.ifmo.practice.seabattle.server;

public class LoginResponse {
    private boolean login = true;
    private boolean serverOk = true;

    public void setLogin(boolean login) {
        this.login = login;
    }

    public void setServerOk(boolean serverOk) {
        this.serverOk = serverOk;
    }
}
