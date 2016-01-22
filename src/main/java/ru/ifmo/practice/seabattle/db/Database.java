package ru.ifmo.practice.seabattle.db;

public interface Database {
    void writeUser(User user);
    User getUser(String nickname);
    void writeMatch(Match match);
    Match[] getMatches(User user);
}
