package ru.yandex.practicum.filmorate.controllers;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.event.Event;
import ru.yandex.practicum.filmorate.model.users.User;
import ru.yandex.practicum.filmorate.service.EventService;
import ru.yandex.practicum.filmorate.service.UserService;

import javax.validation.Valid;
import java.util.*;

@RestController
@Slf4j
@AllArgsConstructor
public class UserController {
    private final UserService userService;
    private final EventService eventService;

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

    @GetMapping("/users/{id}/friends")
    public List<User> getAllFriends(@PathVariable long id) {
        log.info("Получен запрос на получение списка друзей пользователя с id: {}", id);
        return userService.getAllFriends(id);
    }

    @GetMapping("/users/{id}/friends/common/{otherId}")
    public List<User> getCommonFriends(@PathVariable long id, @PathVariable long otherId) {
        log.info("Получен запрос на получение общего списка друзей для пользователей c id {} и {}", id, otherId);
        return userService.getCommonFriends(id, otherId);
    }

    @GetMapping("/users/{id}/feed")
    public List<Event> getUserFeed(@PathVariable long id) {
        log.info("Получен запрос на получение списка событий для пользователя c id {}", id);
        return eventService.getUserEvents(id);
    }

    @PostMapping("/users")
    public User postUser(@Valid @RequestBody User user) {
        log.debug("Получен Post-запрос на добавление пользователя");
        return userService.create(user);
    }

    @PutMapping("/users")
    public User putUser(@Valid @RequestBody User user) {
        log.info("Получен запрос на обновление данных пользователя {}", user);
        return userService.update(user);
    }

    @PutMapping("/users/{id}/friends/{friendId}")
    public void putFriend(@PathVariable long id, @PathVariable long friendId) {
        log.info("Получен запрос на добавления пользователя с id {} в друзья пользователю с id {}", friendId, id);
        userService.addToFriend(id, friendId);
    }

    @DeleteMapping("/users/{id}/friends/{friendId}")
    public void deleteFromFriends(@PathVariable long id, @PathVariable long friendId) {
        log.info("Получен запрос на удаление пользователя с id {} из списка друзей пользователя с id {}", friendId, id);
        userService.deleteFromFriends(id, friendId);
    }

    @DeleteMapping("/users/{userId}")
    public void deleteUser(@PathVariable long userId) {
        log.info("Получен запрос на удаление пользователя с id: {}.", userId);
        userService.delete(userId);
    }
}
