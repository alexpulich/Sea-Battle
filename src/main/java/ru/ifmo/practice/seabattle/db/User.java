package ru.ifmo.practice.seabattle.db;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.List;

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
    private List<Match> wins;
    private List<Match> loses;
    private List<Match> allMatches;

    @Id
    @GeneratedValue(generator = "increment")
    @GenericGenerator(name = "increment", strategy = "increment")
    public int getId() {
        return id;
    }

    @Column(name = "user_nickname", length = 20, unique = true, nullable = false)
    public String getUser_nickname() {
        return user_nickname;
    }

    @Column(name = "email", length = 254, unique = true, nullable = false)
    public String getEmail() {
        return email;
    }

    @Column(name = "password", length = 20, nullable = false)
    public String getPassword() {
        return password;
    }

    @Column(name = "games_count", columnDefinition = "int default 0", nullable = false)
    public int getGames_count() {
        return games_count;
    }

    @Column(name = "wins_count", columnDefinition = "int default 0", nullable = false)
    public int getWins_count() {
        return wins_count;
    }

    @Column(name = "raiting", columnDefinition = "int default 0", nullable = false)
    public int getRaiting() {
        return raiting;
    }

    @Transient
    public List<Match> getLoses() {
        return loses;
    }

    @Transient
    public List<Match> getWins() {
        return wins;
    }

    @Transient
    public List<Match> getAllMatches() {
        return allMatches;
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

    public void setLoses(List<Match> loses) {
        this.loses = loses;
    }

    public void setWins(List<Match> wins) {
        this.wins = wins;
    }

    public void setAllMatches(List<Match> allMatches) {
        this.allMatches = allMatches;
    }

    public User() {
    }

    public User(String user_nickname, String email, String password) {
        this.user_nickname = user_nickname;
        this.email = email;
        this.password = password;
    }
}
