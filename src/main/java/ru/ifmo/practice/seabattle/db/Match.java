package ru.ifmo.practice.seabattle.db;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

@Entity
@Table(name = "Game")
public class Match {

    private int id;
    private int winnerId;
    private int loserId;
    private int winnerEfficiency;
    private int loserEfficiency;

    @Id
    @GeneratedValue(generator = "increment")
    @GenericGenerator(name = "increment", strategy = "increment")
    public int getId() {
        return id;
    }

    public int getWinnerId() {
        return winnerId;
    }

    public int getLoserId() {
        return loserId;
    }

    public int getWinnerEfficiency() {
        return winnerEfficiency;
    }

    public int getLoserEfficiency() {
        return loserEfficiency;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setWinnerId(int winnerId) {
        this.winnerId = winnerId;
    }

    public void setLoserId(int loserId) {
        this.loserId = loserId;
    }

    public void setWinnerEfficiency(int winnerEfficiency) {
        this.winnerEfficiency = winnerEfficiency;
    }

    public void setLoserEfficiency(int loserEfficiency) {
        this.loserEfficiency = loserEfficiency;
    }

    public Match() {
    }
}
