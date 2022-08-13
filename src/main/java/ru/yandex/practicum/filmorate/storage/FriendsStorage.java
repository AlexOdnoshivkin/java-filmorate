package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.users.User;

import java.util.List;

public interface FriendsStorage {
    void addFriend(long reqUserId, long respUserId);

    List<User> getAllFriends(long id);

    List<User> getCommonFriends(long id, long otherId);

    void deleteFromFriends(long id, long otherId);
}
