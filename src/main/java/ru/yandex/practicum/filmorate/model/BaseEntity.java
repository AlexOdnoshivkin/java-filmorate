package ru.yandex.practicum.filmorate.model;

import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

abstract public class BaseEntity {
    private static long count;
    private long id;

    public long getId() {
        return id;
    }

    public void generateId() {
        if(id == 0) {
            id = ++count;
        }
    }

}
