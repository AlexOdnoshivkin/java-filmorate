package ru.yandex.practicum.filmorate.storage.dao;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exceptions.RatingWasAlreadyAdded;
import ru.yandex.practicum.filmorate.model.films.Film;
import ru.yandex.practicum.filmorate.storage.FilmRatingStorage;

@Repository
@AllArgsConstructor
@Slf4j
public class FilmRatingDbStorage implements FilmRatingStorage {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public void addRating(long id, long userId, int rating) {
        String sqlQuery = "INSERT INTO film_ratings (film_id, user_id, user_rating) VALUES (?, ?, ?)";

        int affected = jdbcTemplate.update("UPDATE FILM_RATINGS set " +
                "USER_RATING = ? where FILM_ID = ? AND USER_ID = ?", rating, id);
        if (affected == 0) {
            throw new RatingWasAlreadyAdded("Рейтинг фильму " + id + " от пользователя "+ userId + "уже был добавлен");
        }
        jdbcTemplate.update(sqlQuery, id, userId, rating);
        log.debug("Добавлен рейтинг {} фильму с id {} пользователем с id {}", rating, id, userId);
    }

    public void updateRating(long id, long userId, int rating) {
        String sqlQuery = "UPDATE FILM_RATINGS set USER_RATING = ? where FILM_ID = ? AND USER_ID = ?";
        jdbcTemplate.update(sqlQuery, id, userId, rating);
        log.debug("Обновлён рейтинг {} фильму с id {} пользователем с id {}", rating, id, userId);
    }

    @Override
    public void deleteRating(long id, long userId) {
        String sqlQuery = "DELETE FROM film_ratings WHERE film_id = ? AND user_id = ?";
        jdbcTemplate.update(sqlQuery, id, userId);
        log.debug("Удалён рейтинг у фильма с id {} пользователем с id {}", id, userId);
    }

    public void getFilmRating(Film film) {
        String sqlQuery = "SELECT AVG(USER_RATING) AS RATING " +
                "FROM FILM_RATINGS AS f " +
                "WHERE FILM_ID = ? " +
                "GROUP BY FILM_ID";

        log.debug("Получен рейтинг фильма с id {}", film.getId());
        film.setRating(jdbcTemplate.queryForObject(sqlQuery, Float.class, film.getId()));

    }
}
