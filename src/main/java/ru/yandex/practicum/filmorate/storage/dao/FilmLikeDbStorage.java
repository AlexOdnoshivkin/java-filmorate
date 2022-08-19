package ru.yandex.practicum.filmorate.storage.dao;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.storage.FilmLikeStorage;

@Repository
@AllArgsConstructor
@Slf4j
public class FilmLikeDbStorage implements FilmLikeStorage {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public void addLike(long id, long userId) {
        String sqlQuery = "INSERT INTO LIKES (FILM_ID, USER_ID) VALUES (?, ?)";
        jdbcTemplate.update(sqlQuery, id, userId);
        log.debug("Добавлен лайк фильму с id: {} пользователем с id: {}", id, userId);
    }

    @Override
    public void deleteLike(long id, long userId) {
        String sqlQuery = "DELETE FROM LIKES WHERE FILM_ID = ? AND USER_ID = ?";
        jdbcTemplate.update(sqlQuery, id, userId);
        log.debug("Удалён лайк фильму с id: {} пользователем с id: {}", id, userId);
    }
}
