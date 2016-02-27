package ru.ifmo.practice.seabattle.server;

public class RegistrationResponse {
    private boolean validPassword = true;
    private boolean validPassConfirm = true;
    private boolean validNickname = true;
    private boolean uniqueNickname = true;
    private boolean validEmail = true;
    private boolean uniqueEmail = true;
    private boolean serverOk = true;
    private boolean userRegistered = true;

    public void setValidPassword(boolean validPassword) {
        this.validPassword = validPassword;
    }

    public void setValidPassConfirm(boolean validPassConfirm) {
        this.validPassConfirm = validPassConfirm;
    }

    public void setValidNickname(boolean validNickname) {
        this.validNickname = validNickname;
    }

    public void setUniqueNickname(boolean uniqueNickname) {
        this.uniqueNickname = uniqueNickname;
    }

    public void setValidEmail(boolean validEmail) {
        this.validEmail = validEmail;
    }

    public void setUniqueEmail(boolean uniqueEmail) {
        this.uniqueEmail = uniqueEmail;
    }

    public void setServerOk(boolean serverOk) {
        this.serverOk = serverOk;
    }

    public void setUserRegistered(boolean userRegistered) {
        this.userRegistered = userRegistered;
    }
}
