package ru.ifmo.practice.seabattle.db;

import ru.ifmo.practice.seabattle.db.DAO.Impl.MatchDAOImpl;
import ru.ifmo.practice.seabattle.db.DAO.Impl.UserDAOImpl;

//Класс для получения инстанса класса, реализующего обращение к таблице
public class Factory {
    private static UserDAOImpl userDAOimpl = null;
    private static MatchDAOImpl matchDAOimpl = null;
    private static Factory instance = null;

    public static synchronized Factory getInstance() {
        if (instance == null) {
            instance = new Factory();
        }
        return instance;
    }

    public UserDAOImpl getUserDAOimpl() {
        if (userDAOimpl == null) {
            userDAOimpl = new UserDAOImpl();
        }
        return userDAOimpl;
    }

    public MatchDAOImpl getMatchDAOimpl() {
        if (matchDAOimpl == null) {
            matchDAOimpl = new MatchDAOImpl();
        }
        return matchDAOimpl;
    }
}
