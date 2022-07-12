package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.BaseStorage;

import java.util.List;

public abstract class BaseService<T> {

    protected final BaseStorage<T> storage;

    protected BaseService(BaseStorage<T> storage) {
        this.storage = storage;
    }

    public List<T> getAll() {
        return storage.getAll();
    }

    public T getById(Long id) {
        return storage.getById(id);
    }

    abstract public T create(T t);

   abstract public T update(T t);

}
