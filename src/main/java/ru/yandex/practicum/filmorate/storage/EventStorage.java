package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.event.Event;

import java.util.List;

public interface EventStorage {
    void add(Event event);

    List<Event> getUserEvents(long userId);
}
