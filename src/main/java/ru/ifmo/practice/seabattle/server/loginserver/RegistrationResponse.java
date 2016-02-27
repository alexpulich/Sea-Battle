package ru.ifmo.practice.seabattle.server.loginserver;

public class RegistrationResponse {
    private boolean validPassword;
    private boolean validPassConfirm;
    private boolean validNickname;
    private boolean uniqueNickname;
    private boolean validEmail;
    private boolean uniqueEmail;
    private boolean serverOk;
    private boolean userRegistered;

    public RegistrationResponse(boolean validPassword, boolean validPassConfirm,
                                boolean validNickname, boolean uniqueNickname,
                                boolean validEmail, boolean uniqueEmail,
                                boolean serverOk, boolean userRegistered) {
        this.validPassword = validPassword;
        this.validPassConfirm = validPassConfirm;
        this.validNickname = validNickname;
        this.uniqueNickname = uniqueNickname;
        this.validEmail = validEmail;
        this.uniqueEmail = uniqueEmail;
        this.serverOk = serverOk;
        this.userRegistered = userRegistered;
    }
}
