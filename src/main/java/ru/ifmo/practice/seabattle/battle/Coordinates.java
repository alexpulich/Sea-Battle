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

    public String toString() {
        return "{" + x + "," + y + "}";
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof Coordinates && this.toString().equals(obj.toString());
    }

    @Override
    public int hashCode() {
        int result = 37 * 17 + x;
        result = 37 * result + y;

        return result;
    }
}
