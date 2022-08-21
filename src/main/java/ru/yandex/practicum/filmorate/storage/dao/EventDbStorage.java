package ru.yandex.practicum.filmorate.storage.dao;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.event.Event;
import ru.yandex.practicum.filmorate.model.event.EventType;
import ru.yandex.practicum.filmorate.model.event.Operation;
import ru.yandex.practicum.filmorate.storage.EventStorage;

import java.sql.*;
import java.util.List;

@Repository
@Primary
@AllArgsConstructor
@Slf4j
public class EventDbStorage implements EventStorage {
    private JdbcTemplate jdbcTemplate;

    @Override
    public void add(Event event) {
        String sqlQuery = "INSERT INTO EVENTS (USER_ID, EVENT_TYPE, OPERATION, ENTITY_ID) " +
                "VALUES (?, ?, ?, ?) ";

        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement(sqlQuery, new String[]{"EVENT_ID"});
            stmt.setLong(1, event.getUserId());
            stmt.setString(2, String.valueOf(event.getEventType()));
            stmt.setString(3, String.valueOf(event.getOperation()));
            stmt.setLong(4, event.getEntityId());
            return stmt;
        }, keyHolder);
        event.setEventId(keyHolder.getKey().longValue());
        log.debug("Событие {} записано в базу данных", event);
    }

    @Override
    public List<Event> getUserEvents(long userId) {
        String sqlQuery = "SELECT EVENT_ID," +
                "       TIMESTAMP," +
                "       USER_ID," +
                "       EVENT_TYPE," +
                "       OPERATION," +
                "       ENTITY_ID " +
                "FROM EVENTS " +
                "WHERE USER_ID = ?" +
                "ORDER BY TIMESTAMP";
        return jdbcTemplate.query(sqlQuery, this::mapRowToEvent, userId);
    }

    private Event mapRowToEvent(ResultSet resultSet, int id) throws SQLException {
        Event event = new Event(
                resultSet.getLong("user_id"),
                resultSet.getLong("entity_id")
        );
        event.setEventId(resultSet.getLong("event_id"));
        event.setTimestamp(resultSet.getTimestamp("timestamp").toInstant().toEpochMilli());
        event.setEventType(EventType.valueOf(resultSet.getString("event_type")));
        event.setOperation(Operation.valueOf(resultSet.getString("operation")));
        return event;
    }


}
