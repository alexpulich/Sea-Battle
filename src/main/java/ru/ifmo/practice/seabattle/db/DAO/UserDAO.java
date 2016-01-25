package ru.ifmo.practice.seabattle.db.DAO;

import ru.ifmo.practice.seabattle.db.User;

public interface UserDAO {
    public void addUser(User user);

    public User getUserById(int id);

    public void updateUser(int id, User user);

    public void deleteUser(User user);
}
