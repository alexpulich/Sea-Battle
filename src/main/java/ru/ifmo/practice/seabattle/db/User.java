package ru.ifmo.practice.seabattle.db;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

@Entity
@Table(name = "User")
public class User {
    private int id;
    private String user_nickname;//max length=20
    private String email;        //max length=20
    private String password;     //max length=20
    private int games_count;
    private int wins_count;
    private int raiting;

    @Id
    @GeneratedValue(generator = "increment")
    @GenericGenerator(name = "increment", strategy = "increment")
    public int getId() {
        return id;
    }

    public String getUser_nickname() {
        return user_nickname;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public int getGames_count() {
        return games_count;
    }

    public int getWins_count() {
        return wins_count;
    }

    public int getRaiting() {
        return raiting;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setUser_nickname(String user_nickname) {
        this.user_nickname = user_nickname;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setGames_count(int games_count) {
        this.games_count = games_count;
    }

    public void setWins_count(int wins_count) {
        this.wins_count = wins_count;
    }

    public void setRaiting(int raiting) {
        this.raiting = raiting;
    }

    public User() {
    }

}
