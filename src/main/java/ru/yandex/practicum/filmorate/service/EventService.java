package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.event.Event;
import ru.yandex.practicum.filmorate.model.event.EventType;
import ru.yandex.practicum.filmorate.model.event.Operation;
import ru.yandex.practicum.filmorate.storage.EventStorage;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class EventService {

    private final EventStorage eventStorage;

    public List<Event> getUserEvents(long userId) {
        return eventStorage.getUserEvents(userId);
    }

    public void removeLikeEvent(long userId, long entityId) {
        Event event = new Event(userId, entityId);
        event.setEventType(EventType.LIKE);
        event.setOperation(Operation.REMOVE);
        eventStorage.add(event);
    }

    public void addLikeEvent(long userId, long entityId) {
        Event event = new Event(userId, entityId);
        event.setEventType(EventType.LIKE);
        event.setOperation(Operation.ADD);
        eventStorage.add(event);
    }

    public void removeFriendEvent(long userId, long entityId) {
        Event event = new Event(userId, entityId);
        event.setEventType(EventType.FRIEND);
        event.setOperation(Operation.REMOVE);
        eventStorage.add(event);
    }

    public void addFriendEvent(long userId, long entityId) {
        Event event = new Event(userId, entityId);
        event.setEventType(EventType.FRIEND);
        event.setOperation(Operation.ADD);
        eventStorage.add(event);
    }

    public void removeReviewEvent(long userId, long entityId) {
        Event event = new Event(userId, entityId);
        event.setEventType(EventType.REVIEW);
        event.setOperation(Operation.REMOVE);
        eventStorage.add(event);
    }

    public void addReviewEvent(long userId, long entityId) {
        Event event = new Event(userId, entityId);
        event.setEventType(EventType.REVIEW);
        event.setOperation(Operation.ADD);
        eventStorage.add(event);
    }

    public void updateReviewEvent(long userId, long entityId) {
        Event event = new Event(userId, entityId);
        event.setEventType(EventType.REVIEW);
        event.setOperation(Operation.UPDATE);
        eventStorage.add(event);
    }


}
