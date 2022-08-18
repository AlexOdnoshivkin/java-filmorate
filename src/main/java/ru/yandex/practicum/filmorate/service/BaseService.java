package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.exceptions.EntityNotFoundException;
import ru.yandex.practicum.filmorate.storage.EntityStorage;

import java.util.List;

//Базовый класс для сервисов. Добавил, так как много общих методов
public abstract class BaseService<T> {

    protected final EntityStorage<T> storage;

    protected BaseService(EntityStorage<T> storage) {
        this.storage = storage;
    }

    public List<T> getAll() {
        return storage.getAll();
    }

    public T getById(Long id) {
        if (storage.getById(id) == null) {
            throw new EntityNotFoundException("Объект не найден");
        }
        return storage.getById(id);
    }

    abstract public T create(T t); // сделал эти методы абстрактными, так как в них уже нужны методы конткретного класса

    abstract public T update(T t);

    abstract public void delete(Long id);
}
