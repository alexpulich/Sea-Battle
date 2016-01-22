package ru.ifmo.practice.seabattle.db;

public class Match {
    private User user1;
    private User user2;
    private MatchWinner winner;

    public User getUser1() {
        return user1;
    }

    public User getUser2() {
        return user2;
    }

    public MatchWinner getWinner() {
        return winner;
    }

    public Match(User user1, User user2, MatchWinner winner) {
        this.user1 = user1;
        this.user2 = user2;
        this.winner = winner;
    }
}
