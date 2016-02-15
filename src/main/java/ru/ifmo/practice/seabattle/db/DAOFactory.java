package ru.ifmo.practice.seabattle.db;

import ru.ifmo.practice.seabattle.db.DAO.Impl.MatchDAOImpl;
import ru.ifmo.practice.seabattle.db.DAO.Impl.UserDAOImpl;

//Класс для получения инстанса класса, реализующего обращение к таблице
public class DAOFactory {
    private static UserDAOImpl userDAOimpl = new UserDAOImpl();
    private static MatchDAOImpl matchDAOimpl = new MatchDAOImpl();

    private static class DAOFactoryHolder {
        private static DAOFactory instance = new DAOFactory();
    }

    public static DAOFactory getInstance() {
        return DAOFactoryHolder.instance;
    }

    public UserDAOImpl getUserDAOimpl() {
        return userDAOimpl;
    }

    public MatchDAOImpl getMatchDAOimpl() {
        return matchDAOimpl;
    }
}
