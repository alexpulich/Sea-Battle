package ru.ifmo.practice.seabattle.db.DAO;

import ru.ifmo.practice.seabattle.db.Match;
import ru.ifmo.practice.seabattle.db.User;

import java.sql.SQLException;
import java.util.List;

public interface UserDAO {
    public void addUser(User user) throws SQLException;

    public User getUserById(int id) throws SQLException;

    public void updateUser(int id, User user) throws SQLException;

    public void deleteUser(User user) throws SQLException;

    public boolean isNicknameUnique(String nickname) throws SQLException;

    public boolean isEmailUnique(String email) throws SQLException;

    public List<Match> getWins(int user_id) throws SQLException;

    public List<Match> getLoses(int user_id) throws SQLException;

    public List<Match> getAllMatches(int user_id) throws SQLException;

    public User login(String email, String password);

    public User getUserByNickname(String nickname);
}
