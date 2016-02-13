package ru.ifmo.practice.seabattle.server;

import ru.ifmo.practice.seabattle.exceptions.RoomIsFullException;

class Room {
    private String player1 = null;
    private String player2 = null;

    public String getPlayer1() {
        return player1;
    }

    public String getPlayer2() {
        return player2;
    }

    public boolean isFull() {
        return player1 != null && player2 != null;
    }

    public boolean isEmpity() {
        return player1 == null && player2 == null;
    }

    public Room() {}

    public Room(String player1, String player2) {
        this.player1 = player1;
        this.player2 = player2;
    }

    public void add(String player) {
        if (player1 == null) player1 = player;
        else if (player2 == null) player2 = player;
        else throw new RoomIsFullException();
    }

    public boolean remove(String player) {
        if (player.equals(player1)) {
            player1 = null;
            return true;
        } else if (player.equals(player2)) {
            player2 = null;
            return true;
        } else return false;
    }

    public boolean contains(String player) {
        return player1.equals(player) || player2.equals(player);
    }
}
