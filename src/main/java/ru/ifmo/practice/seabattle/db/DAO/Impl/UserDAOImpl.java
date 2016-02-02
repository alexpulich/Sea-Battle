package ru.ifmo.practice.seabattle.db.DAO.Impl;

import org.hibernate.Query;
import org.hibernate.Session;
import ru.ifmo.practice.seabattle.db.DAO.UserDAO;
import ru.ifmo.practice.seabattle.db.HibernateUtil;
import ru.ifmo.practice.seabattle.db.Match;
import ru.ifmo.practice.seabattle.db.User;

import java.util.ArrayList;
import java.util.List;

public class UserDAOImpl implements UserDAO {
    @Override
    public void addUser(User user) {
        Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            session.beginTransaction();
            session.save(user);
            session.getTransaction().commit();
        } catch (Exception e) {
            //Обработку исключений пока не придумал
            System.out.println(e.toString());
        } finally {
            if (session != null && session.isOpen()) {
                session.close();
            }
        }
    }

    @Override
    public User getUserById(int id) {
        Session session = null;
        User user = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            session.beginTransaction();
            user = (User) session.load(User.class, id);
        } catch (Exception e) {
            //Обработку исключений пока не придумал
            System.out.println(e.toString());
        } finally {
            if (session != null && session.isOpen()) {
                session.close();
            }
        }
        return user;
    }

    @Override
    public void updateUser(int id, User user) {
        Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            session.beginTransaction();
            session.update(user);
            session.getTransaction().commit();
        } catch (Exception e) {
            //Обработку исключений пока не придумал
            System.out.println(e.toString());
        } finally {
            if (session != null && session.isOpen()) {
                session.close();
            }
        }

    }

    @Override
    public void deleteUser(User user) {
        Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            session.beginTransaction();
            session.delete(user);
            session.getTransaction().commit();
        } catch (Exception e) {
            //Обработку исключений пока не придумал
            System.out.println(e.toString());
        } finally {
            if (session != null && session.isOpen()) {
                session.close();
            }
        }
    }

    @Override
    public boolean isNicknameUnique(String nickname) {
        Session session = null;
        boolean flag=false;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            session.beginTransaction();
            Query query = session.
                    createQuery("select 1 from User t where t.user_nickname = :key");
            query.setString("key", nickname);
            flag=(query.uniqueResult() == null);
            session.getTransaction().commit();
        } catch (Exception e) {
            //Обработку исключений пока не придумал
            System.out.println(e.toString());
        } finally {
            if (session != null && session.isOpen()) {
                session.close();
            }
            return (flag);
        }
    }

    @Override
    public boolean isEmailUnique(String email) {
        Session session = null;
        boolean flag=false;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            session.beginTransaction();
            Query query = session.
                    createQuery("select 1 from User t where t.email = :key");
            query.setString("key", email);
            flag=(query.uniqueResult() == null);
            session.getTransaction().commit();
        } catch (Exception e) {
            //Обработку исключений пока не придумал
            System.out.println(e.toString());
        } finally {
            if (session != null && session.isOpen()) {
                session.close();
            }
            return (flag);
        }
    }
    @Override
    public List<Match> getWins(int user_id){
        Session session = null;
        List<Match> wins = new ArrayList<Match>();
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            Query query = session.createQuery("from Match where winner_id=:winnerId")
                    .setLong("winnerId", user_id);
            wins = (List<Match>) query.list();
        } catch (Exception e) {
           System.out.println(e);
        } finally {
            if (session != null && session.isOpen()) {
                session.close();
            }
        }
        return wins;
    }

    @Override
    public List<Match> getLoses(int user_id) {
        Session session = null;
        List<Match> loses = new ArrayList<Match>();
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            Query query = session.createQuery("from Match where loser_id=:loserId")
                    .setLong("loserId", user_id);
            loses = (List<Match>) query.list();
        } catch (Exception e) {
            System.out.println(e);
        } finally {
            if (session != null && session.isOpen()) {
                session.close();
            }
        }
        return loses;
    }
}
