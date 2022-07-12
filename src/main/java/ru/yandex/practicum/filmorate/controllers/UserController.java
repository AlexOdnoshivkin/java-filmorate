package ru.yandex.practicum.filmorate.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import javax.validation.Valid;
import java.util.*;

@RestController
@Slf4j
public class UserController {
    private final UserService userService;

    @Autowired
    public UserController (UserService userService) {
        this.userService = userService;
    }
    @GetMapping(value = {"/users", "/users/{id}"})
    public Collection<User> getUsers(@PathVariable(required = false) Long id) {
        if (id == null) {
            log.info("Получен запрос на получение списка всех пользователей");
            return userService.getAllUsers();
        }
        log.info("Получен Get-запрос на получение пользователя");
        return Collections.singleton(userService.getUserById(id));
    }

    @PostMapping("/users")
    public User postUser(@Valid @RequestBody User user) {
        log.debug("Получен Post-запрос на добавление пользователя");
        return userService.createUser(user);
    }

    @PutMapping("/users")
    public User pusUser(@Valid @RequestBody User user) {
        /*if (users.containsKey(user.getId())) {
            log.debug("Обновлены данные пользователя: {}", user);
            users.put(user.getId(), user);
        } else {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Пользователь не найден");

        }*/
        return user;
    }
}
