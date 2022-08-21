package ru.yandex.practicum.filmorate.model.event;

import lombok.Data;

@Data
public class Event {
    private long timestamp;
    private final long userId;
    private EventType eventType;
    private Operation operation;
    private long eventId;
    private final long entityId;
}
