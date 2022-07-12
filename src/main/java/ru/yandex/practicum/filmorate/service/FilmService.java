package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.EntityNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.BaseStorage;

@Component
@Slf4j
public class FilmService extends  BaseService<Film>{
    @Autowired
    protected FilmService(BaseStorage<Film> storage) {
        super(storage);
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
}
