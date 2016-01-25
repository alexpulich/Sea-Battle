package ru.ifmo.practice.seabattle.db;

public class User {
    private int id;
    private int raiting;
    private String niсkname;//max length 20
    private String password;//max length 20
    private String email;   //max length 20
    private int gamesCount;
    private int winsCount;


    public double getRating() {
        return raiting;
    }

    public String getEmail() {
        return email;
    }

    public String getNiсkname() {
        return niсkname;
    }

    public String getPassword() {
        return password;
    }

    public int getId() {
        return id;
    }

    public int getRaiting() {
        return raiting;
    }

    public int getGamesCount() {
        return gamesCount;
    }

    public int getWinsCount() {
        return winsCount;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setRaiting(int raiting) {
        this.raiting = raiting;
    }

    public void setNiсkname(String niсkname) {
        this.niсkname = niсkname;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setGamesCount(int gamesCount) {
        this.gamesCount = gamesCount;
    }

    public void setWinsCount(int winsCount) {
        this.winsCount = winsCount;
    }

    public User() {
    }

    public User(int raiting, String niсkname, String password) {
        this.raiting = raiting;
        this.niсkname = niсkname;
        this.password = password;
    }

    public User(String niсkname, String password) {
        this.raiting = 0;
        this.niсkname = niсkname;
        this.password = password;
        this.gamesCount = 0;
        this.winsCount = 0;
    }
}
