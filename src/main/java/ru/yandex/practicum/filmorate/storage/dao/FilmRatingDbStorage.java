package ru.yandex.practicum.filmorate.storage.dao;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.storage.FilmRatingStorage;

@Repository
@AllArgsConstructor
@Slf4j
public class FilmRatingDbStorage implements FilmRatingStorage {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public void addRating(long id, long userId, float rating) {
        String sqlQuery = "INSERT INTO film_ratings (film_id, user_id, user_rating) VALUES (?, ?, ?)";
        jdbcTemplate.update(sqlQuery, id, userId, rating);
        log.debug("Добавлен рейтинг {} фильму с id {} пользователем с id {}", rating, id, userId);
    }

    @Override
    public void deleteRating(long id, long userId) {
        String sqlQuery = "DELETE FROM film_ratings WHERE film_id = ? AND user_id = ?";
        jdbcTemplate.update(sqlQuery, id, userId);
        log.debug("Удалён рейтинг у фильма с id {} пользователем с id {}", id, userId);
    }
}
