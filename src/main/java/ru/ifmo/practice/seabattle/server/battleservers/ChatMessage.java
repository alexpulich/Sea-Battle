package ru.ifmo.practice.seabattle.server.battleservers;

public class ChatMessage {
    private String nickName;
    private String message;

    public ChatMessage(String nickName, String message) {
        this.nickName = nickName;
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public String getNickName() {
        return nickName;
    }
}
