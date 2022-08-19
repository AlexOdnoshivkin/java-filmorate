package ru.yandex.practicum.filmorate.storage.dao;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.films.Film;
import ru.yandex.practicum.filmorate.model.films.Mpa;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.stream.Stream;
import java.util.List;

@Repository
@Primary
@AllArgsConstructor
@Slf4j
public class FilmDbStorage implements FilmStorage {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public Film add(Film film) {
        String sqlQuery = "INSERT INTO FILMS (NAME, DESCRIPTION, RELEASE_DATE, DURATION, MPA_ID) " +
                "VALUES (?, ?, ?, ?, ?) ";

        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement(sqlQuery, new String[]{"FILM_ID"});
            stmt.setString(1, film.getName());
            stmt.setString(2, film.getDescription());
            stmt.setDate(3, Date.valueOf(film.getReleaseDate()));
            stmt.setLong(4, film.getDuration());
            stmt.setLong(5, film.getMpa().getId());
            return stmt;
        }, keyHolder);
        film.setId(keyHolder.getKey().longValue());
        log.debug("Фильм {} записан в базу данных", film);
        return film;
    }

    @Override
    public Film update(Film film) {
        String sqlQuery = "UPDATE FILMS SET  name = ?, DESCRIPTION = ?, RELEASE_DATE = ?, DURATION = ?," +
                " MPA_ID = ?  WHERE FILM_ID = ?";

        jdbcTemplate.update(sqlQuery,
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration(),
                film.getMpa().getId(),
                film.getId()
        );
        log.debug("данные фильма {} обновлены в базе данных", film);
        return getById(film.getId());
    }

    @Override
    public void delete(Film film) {
        String sqlQuery = "DELETE FROM FILMS WHERE FILM_ID = ?";
        jdbcTemplate.update(sqlQuery, film.getId());
    }

    @Override
    public List<Film> getAll() {
        String sqlQuery = "SELECT f.FILM_ID," +
                "                 f.NAME," +
                "                 f.DESCRIPTION," +
                "                 f.RELEASE_DATE," +
                "                 f.DURATION," +
                "                 f.MPA_ID," +
                "                 m.NAME " +
                "FROM FILMS AS f " +
                "INNER JOIN MPA m on m.MPA_ID = f.MPA_ID " +
                "GROUP BY f.FILM_ID";
        return jdbcTemplate.query(sqlQuery, this::mapRowToFilm);
    }

    @Override
    public Film getById(long id) {
        String sqlQuery = "SELECT f.FILM_ID," +
                "                 f.NAME," +
                "                 f.DESCRIPTION," +
                "                 f.RELEASE_DATE," +
                "                 f.DURATION," +
                "                 f.MPA_ID," +
                "                 m.NAME " +
                "FROM FILMS AS f " +
                "INNER JOIN MPA m on m.MPA_ID = f.MPA_ID AND f.FILM_ID = ?";

        int affected = jdbcTemplate.update("UPDATE FILMS set FILM_ID = ? where FILM_ID = ?", id, id);
        if (affected == 0) {
            return null;
        }
        return jdbcTemplate.queryForObject(sqlQuery, this::mapRowToFilm, id);
    }

    @Override
    public Stream<Film> getCommonFilms(Long userId, Long friendId) {
        String selectCommonFilms = "SELECT f.*, m.name " +
            "FROM films AS f " +
            "LEFT JOIN mpa AS m ON f.mpa_id = m.mpa_id " +
            "LEFT JOIN likes AS l on f.film_id = l.film_id " +
            "WHERE l.user_id IN (?, ?) " +
            "GROUP BY l.film_id " +
            "HAVING COUNT(l.user_id) = 2 " +
            "ORDER BY COUNT(l.user_id) DESC";
        return jdbcTemplate
            .query(selectCommonFilms, this::mapRowToFilm, userId, friendId)
            .stream();
    }

    private Film mapRowToFilm(ResultSet resultSet, int id) throws SQLException {
        Film film = new Film(
                resultSet.getString("name"),
                resultSet.getString("description"),
                resultSet.getDate("release_date").toLocalDate(),
                resultSet.getLong("duration")
        );
        long FilmId = resultSet.getLong("film_id");
        film.setId(FilmId);
        film.setMpa(new Mpa(resultSet.getLong("mpa_id"), resultSet.getString(7)));
        return film;
    }

    public Stream<Film> getMostPopularFilmsDirector(final Long id) {
        final String selectMostPopularFilms = "SELECT * " +
                "FROM films " +
                "LEFT JOIN likes ON films.film_id = likes.film_id " +
                "LEFT JOIN mpa ON mpa.mpa_id = films.mpa_id " +
                "LEFT JOIN films_directors ON films_directors.film_id = films.film_id " +
                "WHERE director_id = ? " +
                "GROUP BY films.film_id " +
                "ORDER BY COUNT(user_id) DESC ";
        return jdbcTemplate.query(selectMostPopularFilms, this::mapRowToFilm, id)
                .stream();
    }

    public Stream<Film> getSortFilmsDirectorByYear(final Long id) {
        final String selectMostPopularFilms = "SELECT * " +
                "FROM films " +
                "LEFT JOIN likes ON films.film_id = likes.film_id " +
                "LEFT JOIN mpa ON mpa.mpa_id = films.mpa_id " +
                "LEFT JOIN films_directors ON films_directors.film_id = films.film_id " +
                "WHERE director_id = ? " +
                "GROUP BY films.film_id " +
                "ORDER BY FILMS.RELEASE_DATE ";
        return jdbcTemplate.query(selectMostPopularFilms, this::mapRowToFilm, id)
                .stream();
    }
}
