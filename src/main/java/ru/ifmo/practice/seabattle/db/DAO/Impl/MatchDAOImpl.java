package ru.ifmo.practice.seabattle.db.DAO.Impl;

import org.hibernate.Session;
import ru.ifmo.practice.seabattle.db.DAO.MatchDAO;
import ru.ifmo.practice.seabattle.db.HibernateUtil;
import ru.ifmo.practice.seabattle.db.Match;
import ru.ifmo.practice.seabattle.db.User;

import java.sql.SQLException;

public class MatchDAOImpl implements MatchDAO {
    @Override
    public void addMatch(Match match) throws SQLException {
        Session session = null;
        session = HibernateUtil.getSessionFactory().openSession();
        session.beginTransaction();
        session.save(match);
        session.getTransaction().commit();

        if (session != null && session.isOpen()) {
            session.close();
        }
    }

    @Override
    public void deleteMatch(Match match) throws SQLException {
        Session session = null;
        session = HibernateUtil.getSessionFactory().openSession();
        session.beginTransaction();
        session.delete(match);
        session.getTransaction().commit();

        if (session != null && session.isOpen()) {
            session.close();
        }
    }

    @Override
    public Match getMatchById(int id) throws SQLException {
        Session session = null;
        Match match = null;
        session = HibernateUtil.getSessionFactory().openSession();
        session.beginTransaction();
        match = (Match) session.load(Match.class, id);
        if (session != null && session.isOpen()) {
            session.close();
        }
        return match;
    }
}
