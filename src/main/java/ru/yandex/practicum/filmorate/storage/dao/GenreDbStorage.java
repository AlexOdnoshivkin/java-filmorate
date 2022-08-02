package ru.yandex.practicum.filmorate.storage.dao;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.films.Film;
import ru.yandex.practicum.filmorate.model.films.Genre;
import ru.yandex.practicum.filmorate.storage.GenreStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Repository
@AllArgsConstructor
@Slf4j
public class GenreDbStorage implements GenreStorage {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public List<Genre> getAllGenres() {
        String sqlQuery = "SELECT * FROM GENRE";
        List<Genre> genres = jdbcTemplate.query(sqlQuery, this::mapRowToGenre);
        log.debug("Получены жанры из базы данных {}", genres);
        return genres;
    }

    @Override
    public Genre getGenreById(long id) {
        int affected = jdbcTemplate.update("UPDATE GENRE set GENRE_ID = ? where GENRE_ID = ?", id, id);
        if (affected == 0) {
            return null;
        }
        String sqlQuery = "SELECT * FROM GENRE WHERE GENRE_ID = ?";
        return jdbcTemplate.queryForObject(sqlQuery, this::mapRowToGenre, id);
    }

    @Override
    public Film updateFilmGenre(Film film) {
        jdbcTemplate.update("DELETE FROM FILMS_GENRE WHERE FILM_ID = ?", film.getId());
        String sqlQuery = "INSERT INTO FILMS_GENRE (FILM_ID, GENRE_ID) VALUES ( ?, ?)";

        for (Genre genre : film.getGenres()) {
            jdbcTemplate.update(sqlQuery,
                    film.getId(),
                    genre.getId()
            );
        }
        log.debug("Жанры фильма {} обновлены в базе данных", film);
        return film;
    }

    @Override
    public void setFilmGenre(Film film) {
        String sqlQuery = "SELECT g.GENRE_ID," +
                "                 g.NAME " +
                "FROM FILMS_GENRE AS fg " +
                "INNER JOIN GENRE g on g.GENRE_ID = FG.GENRE_ID " +
                "WHERE fg.FILM_ID = ?";
        List<Genre> genres = jdbcTemplate.query(sqlQuery, this::mapRowToGenre, film.getId());
        Set<Genre> genresSet = new HashSet<>(genres);
        film.setGenres(genresSet);
    }

    @Override
    public void addFilmGenre(Film film) {
        Set<Genre> genres = film.getGenres();
        String sqlQuery = "INSERT INTO FILMS_GENRE (FILM_ID, GENRE_ID) VALUES ( ?, ? )";

        for (Genre genre : genres) {
            jdbcTemplate.update(sqlQuery, film.getId(), genre.getId());
            log.debug("Для фильма {} записан жанр {}", film.getId(), genre.getId());
        }
    }

    public Set<Genre> getFilmGenres(Film film) {
        Set<Genre> genres;
        String sqlQuery = "SELECT g.GENRE_ID," +
                "                 g.NAME " +
                "FROM FILMS_GENRE AS fg " +
                "INNER JOIN GENRE g on g.GENRE_ID = FG.GENRE_ID " +
                "WHERE fg.FILM_ID = ?";
        genres = new HashSet<>(jdbcTemplate.query(sqlQuery, this::mapRowToGenre, film.getId()));
        return genres;
    }

    private Genre mapRowToGenre(ResultSet resultSet, int id) throws SQLException {
        log.debug("Получены жанр c id {} из базы данных", id);
        return new Genre(resultSet.getLong("genre_id"), resultSet.getString("name"));
    }
}
