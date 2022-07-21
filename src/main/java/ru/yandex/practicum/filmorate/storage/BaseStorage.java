package ru.yandex.practicum.filmorate.storage;

import java.util.List;

// Базовый интерфейс для хранилищ. Создал, так как функции в них повторяются
public interface BaseStorage<T> {
    public void add(T t);

    public void update(T t);

    public void delete(T t);

    public List<T> getAll();

    public T getById(long id);
}
