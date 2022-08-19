package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.EntityNotFoundException;
import ru.yandex.practicum.filmorate.model.films.Director;
import ru.yandex.practicum.filmorate.storage.dao.DirectorDBStorage;

import java.util.Collection;

@Service
@Slf4j
public class DirectorService {
    private final DirectorDBStorage directorDBStorage;

    @Autowired
    public DirectorService(DirectorDBStorage directorDBStorage) {
        this.directorDBStorage = directorDBStorage;
    }

    public Collection<Director> getAll() {
        return directorDBStorage.getAllDirectors();
    }

    public Director getById(long id) {
        Director director = directorDBStorage.getById(id);
        if (director == null) {
            throw new EntityNotFoundException("Объект не найден");
        }
        log.debug("Получен режиссер: {}", director);
        return director;
    }

    public Director create(Director director) {
        director = directorDBStorage.add(director);
        log.debug("Добавлен режиссер: {}", director);
        return director;
    }

    public Director update(Director director) {
        Director newDirector = directorDBStorage.change(director);
        if (newDirector == null) {
            throw new EntityNotFoundException("Объект не найден");
        }
        return newDirector;
    }

    public void deleteFromDirector(long id) {
        directorDBStorage.delete(id);
    }
}
