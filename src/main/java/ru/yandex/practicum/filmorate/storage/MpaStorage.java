package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.films.Mpa;

import java.util.List;

public interface MpaStorage {
    public List<Mpa> getAllMpa();

    public Mpa getMpaById(long id);
}
