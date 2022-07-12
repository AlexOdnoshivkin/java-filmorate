package ru.yandex.practicum.filmorate.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
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
    @GetMapping("/users")
    public Collection<User> getUsers() {
        log.info("Получен запрос на получение списка всех пользователей");
        return userService.getAll();
    }

    @GetMapping("/users/{id}")
    public User getUserById(@PathVariable long id) {
        log.info("Получен запрос на получение пользователя c id: {}", id);
        return userService.getById(id);
    }

    @PostMapping("/users")
    public User postUser(@Valid @RequestBody User user) {
        log.debug("Получен Post-запрос на добавление пользователя");
        return userService.create(user);
    }

    @PutMapping("/users")
    public User pusUser(@Valid @RequestBody User user) {
       return userService.update(user);
    }
}
