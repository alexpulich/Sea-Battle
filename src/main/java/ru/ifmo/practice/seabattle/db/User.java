package ru.ifmo.practice.seabattle.db;

public class User {
    private double rating;
    private String niсkname;
    private String password;

    public double getRating() {
        return rating;
    }

    public String getNiсkname() {
        return niсkname;
    }

    public String getPassword() {
        return password;
    }

    public User(double rating, String niсkname, String password) {
        this.rating = rating;
        this.niсkname = niсkname;
        this.password = password;
    }

    public User(String niсkname, String password) {
        rating = 0;
        this.niсkname = niсkname;
        this.password = password;
    }
}
