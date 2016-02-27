package ru.ifmo.practice.seabattle.server;

import com.google.gson.Gson;
import ru.ifmo.practice.seabattle.Console;

public class ResponsesTest {
    public static void main(String[] args) {
        LoginResponse loginResponse = new LoginResponse();
        loginResponse.setLogin(false);
        loginResponse.setServerOk(true);

        Console.outputMessage(new Gson().toJson(new Message<>(loginResponse)));

        RegistrationResponse registrationResponse = new RegistrationResponse();
        registrationResponse.setServerOk(true);
        registrationResponse.setUniqueEmail(true);
        registrationResponse.setUniqueEmail(false);
        registrationResponse.setUniqueNickname(false);
        registrationResponse.setUserRegistered(false);
        registrationResponse.setValidEmail(true);
        registrationResponse.setValidNickname(true);
        registrationResponse.setValidPassConfirm(true);
        registrationResponse.setValidPassword(true);

        Console.outputMessage(new Gson().toJson(new Message<>(registrationResponse)));
    }
}
