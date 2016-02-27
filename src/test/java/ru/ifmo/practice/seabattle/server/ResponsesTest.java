package ru.ifmo.practice.seabattle.server;

import com.google.gson.Gson;
import ru.ifmo.practice.seabattle.Console;
import ru.ifmo.practice.seabattle.server.loginserver.LoginResponse;
import ru.ifmo.practice.seabattle.server.loginserver.RegistrationResponse;

public class ResponsesTest {
    public static void main(String[] args) {
        LoginResponse loginResponse = new LoginResponse(false, true);

        Console.outputMessage(new Gson().toJson(new Message<>(loginResponse)));

        RegistrationResponse registrationResponse = new RegistrationResponse(
                true,
                true,
                true,
                true,
                true,
                false,
                true,
                false
        );

        Console.outputMessage(new Gson().toJson(new Message<>(registrationResponse)));
    }
}
