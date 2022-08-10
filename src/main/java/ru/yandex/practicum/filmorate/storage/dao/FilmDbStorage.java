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
import java.util.List;

@Repository
@Primary
@AllArgsConstructor
@Slf4j
public class FilmDbStorage implements FilmStorage {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public Film add(Film film) {
        String sqlQuery = "INSERT INTO FILMS (NAME, DESCRIPTION, RELEASE_DATE, DURATION, MPA_ID) VALUES (?, ?, ?, ?, ?) ";

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
        String sqlQuery = "SELECT\n" +
                "    f.FILM_ID,\n" +
                "    f.NAME,\n" +
                "    f.DESCRIPTION,\n" +
                "    f.RELEASE_DATE,\n" +
                "    f.DURATION,\n" +
                "    f.MPA_ID,\n" +
                "    m.NAME\n" +
                "FROM FILMS AS f\n" +
                "INNER JOIN MPA m on m.MPA_ID = f.FILM_ID\n" +
                "GROUP BY f.FILM_ID";
        return jdbcTemplate.query(sqlQuery, this::mapRowToFilm);
    }

    @Override
    public Film getById(long id) {
        String sqlQuery = "SELECT\n" +
                "    f.FILM_ID,\n" +
                "    f.NAME,\n" +
                "    f.DESCRIPTION,\n" +
                "    f.RELEASE_DATE,\n" +
                "    f.DURATION,\n" +
                "    f.MPA_ID,\n" +
                "    m.NAME\n" +
                "FROM FILMS AS f\n" +
                "INNER JOIN MPA m on m.MPA_ID = f.MPA_ID AND f.FILM_ID = ?";

        int affected = jdbcTemplate.update("UPDATE FILMS set FILM_ID = ? where FILM_ID = ?", id, id);
        if (affected == 0) {
            return null;
        }
        return jdbcTemplate.queryForObject(sqlQuery, this::mapRowToFilm, id);
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
}
