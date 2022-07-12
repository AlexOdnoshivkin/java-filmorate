package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.EntityNotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.BaseStorage;

@Service
@Slf4j
public class UserService extends BaseService<User>{



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


}
