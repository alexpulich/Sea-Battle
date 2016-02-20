package ru.ifmo.practice.seabattle.server;


import ru.ifmo.practice.seabattle.db.DAOFactory;
import ru.ifmo.practice.seabattle.db.Match;
import ru.ifmo.practice.seabattle.db.User;

import java.sql.SQLException;

public class BattleResultHandler {
    public void handle(String winner_nickname, String loser_nickname) {
        User winner = null;
        User loser = null;
        try {
            winner = DAOFactory.getInstance().getUserDAOimpl().getUserByNickname(winner_nickname);
            loser = DAOFactory.getInstance().getUserDAOimpl().getUserByNickname(loser_nickname);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        winner.setRaiting(winner.getRaiting() + 100);
        loser.setRaiting(loser.getRaiting() + 50);
        winner.setGames_count(winner.getGames_count() + 1);
        loser.setGames_count(loser.getGames_count() + 1);
        winner.setWins_count(winner.getWins_count() + 1);

        try {
            DAOFactory.getInstance().getUserDAOimpl().updateUser(winner.getId(), winner);
            DAOFactory.getInstance().getUserDAOimpl().updateUser(loser.getId(), loser);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        Match match = new Match();

        match.setWinner_nickname(winner.getUser_nickname());
        match.setLoser_nickname(loser.getUser_nickname());
        match.setWinner_eff(0); //Поле может быть использовано для более детального рассчета рейтинга
        match.setLoser_eff(0);  //Поле может быть использовано для более детального рассчета рейтинга
        match.setWinner_id(winner.getId());
        match.setLoser_id(loser.getId());

        try {
            DAOFactory.getInstance().getMatchDAOimpl().addMatch(match);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
