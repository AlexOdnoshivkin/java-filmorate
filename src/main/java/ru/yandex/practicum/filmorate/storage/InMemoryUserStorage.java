package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component(value = "a")
@Scope("singleton")
@Slf4j
public class InMemoryUserStorage implements UserStorage {

    private final Map<Long, User> users = new HashMap<>();

    @Override
    public void add(User user) {
        users.put(user.getId(), user);
    }

    @Override
    public void update(User user) {
        users.put(user.getId(), user);
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

