package ru.ifmo.practice.seabattle.db;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

@Entity
@Table(name = "Game")
public class Match {

    private int id;
    private int loser_id;
    private int winner_id;
    private int winner_eff;
    private int loser_eff;
    private String loser_nickname;
    private String winner_nickname;


    @Id
    @GeneratedValue(generator = "increment")
    @GenericGenerator(name = "increment", strategy = "increment")
    public int getId() {
        return id;
    }


    public int getLoser_id() {
        return loser_id;
    }

    public int getWinner_id() {
        return winner_id;
    }

    public int getWinner_eff() {
        return winner_eff;
    }

    public int getLoser_eff() {
        return loser_eff;
    }

    public String getLoser_nickname() {
        return loser_nickname;
    }

    public String getWinner_nickname() {
        return winner_nickname;
    }

    public void setLoser_eff(int loser_eff) {
        this.loser_eff = loser_eff;
    }

    public void setWinner_id(int winner_id) {
        this.winner_id = winner_id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setLoser_id(int loser_id) {
        this.loser_id = loser_id;
    }

    public void setWinner_eff(int winner_eff) {
        this.winner_eff = winner_eff;
    }

    public void setLoser_nickname(String loser_nickname) {
        this.loser_nickname = loser_nickname;
    }

    public void setWinner_nickname(String winner_nickname) {
        this.winner_nickname = winner_nickname;
    }

    public Match() {
    }
}
