package ru.ifmo.practice.seabattle.db.DAO.Impl;

import org.hibernate.Session;
import ru.ifmo.practice.seabattle.db.DAO.MatchDAO;
import ru.ifmo.practice.seabattle.db.HibernateUtil;
import ru.ifmo.practice.seabattle.db.Match;

public class MatchDAOImpl implements MatchDAO {
    @Override
    public void addMatch(Match match) {
        Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            session.beginTransaction();
            session.save(match);
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
    public void deleteMatch(Match match) {
        Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            session.beginTransaction();
            session.delete(match);
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
}
