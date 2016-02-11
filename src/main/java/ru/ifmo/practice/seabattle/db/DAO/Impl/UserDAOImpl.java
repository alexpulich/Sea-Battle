package ru.ifmo.practice.seabattle.db.DAO.Impl;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import ru.ifmo.practice.seabattle.db.DAO.UserDAO;
import ru.ifmo.practice.seabattle.db.HibernateUtil;
import ru.ifmo.practice.seabattle.db.Match;
import ru.ifmo.practice.seabattle.db.User;

import java.sql.SQLException;
import java.util.List;

public class UserDAOImpl implements UserDAO {

    public interface Command<T> {
        T process(Session session);
    }

    private <T> T transaction(final Command<T> command) {
        final Session session = HibernateUtil.getSessionFactory().openSession();
        final Transaction tx = session.beginTransaction();
        try {
            return command.process(session);
        } finally {
            tx.commit();
            session.close();
        }
    }

    @Override
    public void addUser(User user) throws SQLException {
        transaction((Session session) -> {
            session.save(user);
            return null;
        });
    }

    @Override
    public User getUserById(int id) throws SQLException {
        return transaction((Session session) -> session.load(User.class, id));
    }

    //Для работы update необходимо сначала получить User из таблицы через get, изменить и апдейтить уже его
    @Override
    public void updateUser(int id, User user) throws SQLException {
        transaction((Session session) -> {
            session.update(user);
            return null;
        });
    }

    //Для работы Delete необходимо сначала получить User из таблицы через get, и передавать его как параметр для удаления
    @Override
    public void deleteUser(User user) throws SQLException {
        transaction((Session session) -> {
            session.delete(user);
            return null;
        });
    }

    @Override
    public boolean isNicknameUnique(String nickname) throws SQLException {
        return transaction((Session session) -> {
            boolean flag = false;
            Query query = session.
                    createQuery("select 1 from User t where t.user_nickname = :key");
            query.setString("key", nickname);
            return (query.uniqueResult() == null);
        });
    }

    @Override
    public boolean isEmailUnique(String email) throws SQLException {
        return transaction((Session session) -> {
            boolean flag = false;
            Query query = session.
                    createQuery("select 1 from User t where t.email = :key");
            query.setString("key", email);
            return (query.uniqueResult() == null);
        });
    }

    @Override
    public List<Match> getWins(int user_id) throws SQLException {
        return transaction((Session session) -> {
            return (List<Match>) session.createQuery("from Match where winner_id=:winnerId")
                    .setLong("winnerId", user_id).list();
        });
    }

    @Override
    public List<Match> getLoses(int user_id) throws SQLException {
        return transaction((Session session) -> {
            return (List<Match>) session.createQuery("from Match where loser_id=:loserId")
                    .setLong("loserId", user_id).list();
        });
    }

    @Override
    public List<Match> getAllMatches(int user_id) throws SQLException {
        return transaction((Session session) -> {
            return (List<Match>) session.createQuery("from Match where loser_id=:userId or winner_id=:userId")
                    .setLong("userId", user_id).list();
        });
    }
}
