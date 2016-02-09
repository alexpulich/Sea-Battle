package ru.ifmo.practice.seabattle.db.DAO.Impl;

import org.hibernate.Query;
import org.hibernate.Session;
import ru.ifmo.practice.seabattle.db.DAO.UserDAO;
import ru.ifmo.practice.seabattle.db.HibernateUtil;
import ru.ifmo.practice.seabattle.db.Match;
import ru.ifmo.practice.seabattle.db.User;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class UserDAOImpl implements UserDAO {
    @Override
    public void addUser(User user) throws SQLException {
        Session session = null;

        session = HibernateUtil.getSessionFactory().openSession();
        session.beginTransaction();
        session.save(user);
        session.getTransaction().commit();

        if (session != null && session.isOpen()) {
            session.close();
        }
    }

    @Override
    public User getUserById(int id) throws SQLException {
        Session session = null;
        User user = null;

        session = HibernateUtil.getSessionFactory().openSession();
        session.beginTransaction();
        user = (User) session.load(User.class, id);

        if (session != null && session.isOpen()) {
            session.close();
        }
        return user;
    }

    @Override
    public void updateUser(int id, User user) throws SQLException {
        Session session = null;
        session = HibernateUtil.getSessionFactory().openSession();
        session.beginTransaction();
        session.update(user);
        session.getTransaction().commit();

        if (session != null && session.isOpen()) {
            session.close();
        }
    }

    @Override
    public void deleteUser(User user) throws SQLException {
        Session session = null;

        session = HibernateUtil.getSessionFactory().openSession();
        session.beginTransaction();
        session.delete(user);
        session.getTransaction().commit();

        if (session != null && session.isOpen()) {
            session.close();
        }
    }

    @Override
    public boolean isNicknameUnique(String nickname) throws SQLException {
        Session session = null;
        boolean flag = false;

        session = HibernateUtil.getSessionFactory().openSession();
        session.beginTransaction();
        Query query = session.
                createQuery("select 1 from User t where t.user_nickname = :key");
        query.setString("key", nickname);
        flag = (query.uniqueResult() == null);
        session.getTransaction().commit();

        if (session != null && session.isOpen()) {
            session.close();
        }
        return (flag);
    }

    @Override
    public boolean isEmailUnique(String email) throws SQLException {
        Session session = null;
        boolean flag = false;

        session = HibernateUtil.getSessionFactory().openSession();
        session.beginTransaction();
        Query query = session.
                createQuery("select 1 from User t where t.email = :key");
        query.setString("key", email);
        flag = (query.uniqueResult() == null);
        session.getTransaction().commit();


        if (session != null && session.isOpen()) {
            session.close();
        }
        return (flag);
    }

    @Override
    public List<Match> getWins(int user_id) throws SQLException {
        Session session = null;
        List<Match> wins = new ArrayList<Match>();

        session = HibernateUtil.getSessionFactory().openSession();
        Query query = session.createQuery("from Match where winner_id=:winnerId")
                .setLong("winnerId", user_id);
        wins = (List<Match>) query.list();

        if (session != null && session.isOpen()) {
            session.close();
        }
        return wins;
    }

    @Override
    public List<Match> getLoses(int user_id) throws SQLException {
        Session session = null;
        List<Match> loses = new ArrayList<Match>();

        session = HibernateUtil.getSessionFactory().openSession();
        Query query = session.createQuery("from Match where loser_id=:loserId")
                .setLong("loserId", user_id);
        loses = (List<Match>) query.list();

        if (session != null && session.isOpen()) {
            session.close();
        }
        return loses;
    }

    @Override
    public List<Match> getAllMatches(int user_id) throws SQLException {
        Session session = null;
        List<Match> matches = new ArrayList<Match>();

        session = HibernateUtil.getSessionFactory().openSession();
        Query query = session.createQuery("from Match where loser_id=:userId or winner_id=:userId")
                .setLong("userId", user_id);
        matches = (List<Match>) query.list();

        if (session != null && session.isOpen()) {
            session.close();
        }
        return matches;
    }
}
