package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.users.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component()
@Slf4j
public class InMemoryUserStorage implements UserStorage {

    private final Map<Long, User> users = new HashMap<>();

    @Override
    public User add(User user) {
        users.put(user.getId(), user);
        log.debug("Добавлен пользователь: {}", user);
        return users.get(user.getId());
    }

    @Override
    public User update(User user) {
        return users.put(user.getId(), user);
    }

    @Override
    public void delete(User user) {
        users.remove(user.getId());
    }

    @Override
    public List<User> getAll() {
        log.debug("Переданы все пользователи, количество пользователей: {}", users.size());
        return new ArrayList<>(users.values());
    }

    @Override
    public User getById(long id) {
        log.debug("Передан пользователь: {}", users.get(id));
        return users.get(id);
    }
}
