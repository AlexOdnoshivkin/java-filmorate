package ru.yandex.practicum.filmorate.storage;

import java.util.List;

// Базовый интерфейс для хранилищ. Создал, так как функции в них повторяются
public interface EntityStorage<T> {
    T add(T t);

    T update(T t);

    void delete(T t);

    List<T> getAll();

    T getById(long id);
}
