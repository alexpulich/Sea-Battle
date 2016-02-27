package ru.ifmo.practice.seabattle.db.DAO;

import ru.ifmo.practice.seabattle.db.Match;

import java.sql.SQLException;

public interface MatchDAO {
    public void addMatch(Match match) throws SQLException;

    public void deleteMatch(Match match) throws SQLException;

    public Match getMatchById(int id) throws SQLException;
}
