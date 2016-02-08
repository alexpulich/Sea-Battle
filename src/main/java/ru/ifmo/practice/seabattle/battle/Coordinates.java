package ru.ifmo.practice.seabattle.battle;

public class Coordinates {
    private int x;
    private int y;

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public Coordinates(int x, int y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public int hashCode() {
        int result = 17 * 37 + x;
        result += 17 * result + y;
        return result;
    }

    @Override
    public String toString() {
        return "{" + x + "," + y + "}";
    }
}
