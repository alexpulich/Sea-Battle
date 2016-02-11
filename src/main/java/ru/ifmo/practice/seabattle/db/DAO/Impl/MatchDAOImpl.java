package ru.ifmo.practice.seabattle.db.DAO.Impl;

import org.hibernate.Session;
import ru.ifmo.practice.seabattle.db.DAO.MatchDAO;
import ru.ifmo.practice.seabattle.db.HibernateUtil;
import ru.ifmo.practice.seabattle.db.Match;

import java.sql.SQLException;

public class MatchDAOImpl implements MatchDAO {
    @Override
    public void addMatch(Match match) throws SQLException {
        Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            session.beginTransaction();
            session.save(match);
            session.getTransaction().commit();
        } finally {
            if (session != null && session.isOpen()) {
                session.close();
            }
        }
    }

    //Для работы Delete необходимо сначала получить Match из таблицы через get, и передавать его как параметр для удаления
    @Override
    public void deleteMatch(Match match) throws SQLException {
        Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            session.beginTransaction();
            session.delete(match);
            session.getTransaction().commit();

        } finally {
            if (session != null && session.isOpen()) {
                session.close();
            }
        }
    }

    @Override
    public Match getMatchById(int id) throws SQLException {
        Session session = null;
        Match match = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            session.beginTransaction();
            match = (Match) session.load(Match.class, id);
        } finally {
            if (session != null && session.isOpen()) {
                session.close();
            }
        }
        return match;
    }
}
