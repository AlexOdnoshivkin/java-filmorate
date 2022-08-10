package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.users.User;

import java.util.List;

public interface FriendsStorage {
    public void addFriend(long reqUserId, long respUserId);

    public List<User> getAllFriends(long id);

    public List<User> getCommonFriends(long id, long otherId);

    public void deleteFromFriends(long id, long otherId);
}
