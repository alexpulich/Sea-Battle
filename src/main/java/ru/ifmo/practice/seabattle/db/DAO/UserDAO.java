package ru.ifmo.practice.seabattle.db.DAO;

import ru.ifmo.practice.seabattle.db.Match;
import ru.ifmo.practice.seabattle.db.User;

import java.util.List;

public interface UserDAO {
    public void addUser(User user);

    public User getUserById(int id);

    public void updateUser(int id, User user);

    public void deleteUser(User user);

    public boolean isNicknameUnique(String nickname);

    public boolean isEmailUnique(String email);

    public List<Match> getWins(int user_id);

    public List<Match> getLoses(int user_id);
}
