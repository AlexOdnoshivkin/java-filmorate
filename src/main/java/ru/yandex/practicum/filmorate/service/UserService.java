package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.EntityNotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.BaseStorage;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class UserService extends BaseService<User> {

    @Autowired
    protected UserService(BaseStorage<User> storage) {
        super(storage);
    }

    @Override
    public User create(User user) {
        user.generateId();
        if (user.getName() == null || user.getName().isEmpty()) {
            user.setName(user.getLogin());
        }
        storage.add(user);
        log.debug("Добавлен пользователь: {}", user);
        return storage.getById(user.getId());
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

    public void addToFriend(long id, long friendId) {
        User user = storage.getById(id);
        User friend = storage.getById(friendId);
        if (user == null || friend == null) {
            throw new EntityNotFoundException("Пользователь не найден");
        }
        user.addToFriend(friendId);
        storage.update(user);
        friend.addToFriend(id);
        storage.update(friend);
    }

    public List<User> getAllFriends(long id) {
        User user = storage.getById(id);
        if (user == null) {
            throw new EntityNotFoundException("Пользователь не найден");
        }
        return user.getFriends().stream()
                .map(storage::getById)
                .collect(Collectors.toList());
    }

    public List<User> getCommonFriends(long id, long otherId) {
        User user = storage.getById(id);
        User otherUser = storage.getById(otherId);
        if (user == null || otherUser == null) {
            throw new EntityNotFoundException("Пользователь не найден");
        }
        return user.getFriends().stream()
                .filter((id1) -> otherUser.getFriends().stream()
                        .anyMatch((id2) ->id2.equals(id1)))
                .map(storage::getById)
                .collect(Collectors.toList());
        /*return user.getFriends().stream()
                .filter((id1) -> {
                    boolean commonFriend = false;
                    for (Long id2 : otherUser.getFriends()) {
                        if (id1.equals(id2)) {
                            commonFriend = true;
                            break;
                        }
                    }
                    return commonFriend;
                })
                .map(storage::getById)
                .collect(Collectors.toList());*/
    }

    public void deleteFromFriends(long id, long otherId) {
        User user = storage.getById(id);
        User otherUser = storage.getById(otherId);
        if (user == null || otherUser == null) {
            throw new EntityNotFoundException("Пользователь не найден");
        }
        user.deleteFromFriends(otherId);
        otherUser.deleteFromFriends(id);
        storage.update(user);
        storage.update(otherUser);
    }


}
