package ru.ifmo.practice.seabattle.server;

public class Message<T> {
    private String type;
    private T data;

    public String getType() {
        return type;
    }

    public T getData() {
        return data;
    }

    public Message (T data) {
        type = data.getClass().getSimpleName();
        this.data = data;
    }
}
