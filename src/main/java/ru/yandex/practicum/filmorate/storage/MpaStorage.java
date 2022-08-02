package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.films.Mpa;

import java.util.List;

public interface MpaStorage {
    List<Mpa> getAllMpa();

    Mpa getMpaById(long id);
}
