package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.EntityNotFoundException;
import ru.yandex.practicum.filmorate.model.users.User;
import ru.yandex.practicum.filmorate.storage.EntityStorage;
import ru.yandex.practicum.filmorate.storage.FilmLikeStorage;
import ru.yandex.practicum.filmorate.storage.FriendsStorage;

import java.util.List;

@Service
@Slf4j
public class UserService extends BaseService<User> {
    private final FriendsStorage friendsStorage;
    protected final EntityStorage<User> storage;
    private final EventService eventService;

    @Autowired
    public UserService(EntityStorage<User> storage, FriendsStorage friendsStorage, EventService eventService) {
        super(storage);
        this.friendsStorage = friendsStorage;
        this.storage = storage;
        this.eventService = eventService;
    }

    @Override
    public User getById(Long id) {
        if (storage.getById(id) == null) {
            throw new EntityNotFoundException("Объект не найден");
        }
        return storage.getById(id);
    }

    @Override
    public User create(User user) {
        user.generateId();
        if (user.getName() == null || user.getName().isEmpty()) {
            user.setName(user.getLogin());
        }
        return storage.add(user);
    }

    @Override
    public User update(User user) {
        if (storage.getById(user.getId()) != null) {
            log.debug("Обновлены данные пользователя: {}", user);
            storage.update(user);
        } else {
            throw new EntityNotFoundException("Пользователь не найден");
        }
        return storage.getById(user.getId());
    }

    @Override
    public void delete(Long id) {
        User user = storage.getById(id);
        if (user == null) {
            throw new EntityNotFoundException("Пользователь не найден");
        }
        storage.delete(user);
    }

    public void addToFriend(long id, long friendId) {
        User user = storage.getById(id);
        User friend = storage.getById(friendId);
        if (user == null || friend == null) {
            throw new EntityNotFoundException("Пользователь не найден");
        }
        friendsStorage.addFriend(id, friendId);
        eventService.addFriendEvent(id, friendId);
    }

    public List<User> getAllFriends(long id) {
        User user = storage.getById(id);
        if (user == null) {
            throw new EntityNotFoundException("Пользователь не найден");
        }
        return friendsStorage.getAllFriends(id);
    }

    public List<User> getCommonFriends(long id, long otherId) {
        User user = storage.getById(id);
        User otherUser = storage.getById(otherId);
        if (user == null || otherUser == null) {
            throw new EntityNotFoundException("Пользователь не найден");
        }
        return friendsStorage.getCommonFriends(id, otherId);
    }

    public void deleteFromFriends(long id, long otherId) {
        User user = storage.getById(id);
        User otherUser = storage.getById(otherId);
        if (user == null || otherUser == null) {
            throw new EntityNotFoundException("Пользователь не найден");
        }
        friendsStorage.deleteFromFriends(id, otherId);
        eventService.removeFriendEvent(id, otherId);
    }
}
