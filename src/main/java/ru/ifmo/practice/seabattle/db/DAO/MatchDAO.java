package ru.ifmo.practice.seabattle.db.DAO;

import ru.ifmo.practice.seabattle.db.Match;

public interface MatchDAO {
    public void addMatch(Match match);

    public void deleteUser(Match match);
}
