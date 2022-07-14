package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.EntityNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.BaseStorage;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Scope("singleton")
@Slf4j
public class FilmService extends  BaseService<Film>{
    private final UserService userService;
    @Autowired
    protected FilmService(BaseStorage<Film> storage, UserService userService) {
        super(storage);
        this.userService = userService;
    }

    @Override
    public Film create(Film film) {
        film.generateId();
        storage.add(film);
        log.debug("Добавлен фильм: {}", film);
        return storage.getById(film.getId());
    }

    @Override
    public Film update(Film film) {
        if (storage.getById(film.getId()) != null) {
            log.debug("Обновлён фильм: {}", film);
            storage.update(film);
        } else {
            throw new EntityNotFoundException("Фильм не найден");
        }
        return storage.getById(film.getId());
    }

    public void addLike(long id, long userId) {
        if (userService.storage.getById(userId) == null) {
            throw new EntityNotFoundException("Пользователь не найден");
        }
        Film film = storage.getById(id);
        if (storage.getById(id) == null) {
            throw new EntityNotFoundException("Фильм не найден");
        }
        film.addLike(userId);
        log.debug("Добавлен лайк фильму с id: {} пользователем с id: {}", id, userId);
        storage.update(film);
    }

    public void deleteLike(long id, long userId) {
        if (userService.storage.getById(userId) == null) {
            throw new EntityNotFoundException("Пользователь не найден");
        }
        Film film = storage.getById(id);
        if (storage.getById(id) == null) {
            throw new EntityNotFoundException("Фильм не найден");
        }
        film.deleteLike(userId);
        log.debug("Удалён лайк фильму с id: {} пользователем с id: {}", id, userId);
        storage.update(film);
    }

    public List<Film> getMostPopularFilms(int count) {
        return storage.getAll().stream()
                .sorted(Comparator.comparingInt(f -> -f.getLikes().size()))
                .limit(count)
                .collect(Collectors.toList());
    }

}
