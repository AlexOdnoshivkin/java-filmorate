package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

public interface UserStorage {
    public void addUser(User user);

    public void updateUser(User user);

    public void deleteUser(User user);

    public List<User> getAllUsers();

    public User getUserById(long id);
}
