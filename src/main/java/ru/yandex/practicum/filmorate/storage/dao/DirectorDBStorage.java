package ru.yandex.practicum.filmorate.storage.dao;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.films.Director;
import ru.yandex.practicum.filmorate.model.films.Film;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Repository
@Primary
@AllArgsConstructor
@Slf4j
public class DirectorDBStorage {
    private final JdbcTemplate jdbcTemplate;

    public Director add(Director director) {
        String sqlQuery = "insert into DIRECTORS(DIRECTOR_NAME) values (?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement(sqlQuery, new String[]{"DIRECTOR_ID"});
            stmt.setString(1, director.getName());
            return stmt;
        }, keyHolder);
        director.setId(keyHolder.getKey().longValue());
        log.debug("Режиссер {} записан в базу данных", director);
        return director;
    }

    public void addFilmDirector(Film film) {
        Set<Director> directors = film.getDirectors();
        String sqlQuery = "INSERT INTO FILMS_DIRECTORS (FILM_ID, DIRECTOR_ID) VALUES ( ?, ? )";
        for (Director director : directors) {
            jdbcTemplate.update(sqlQuery, film.getId(), director.getId());
            log.debug("Для фильма {} записан режиссер {}", film.getId(), director.getId());
        }
    }

    public Collection<Director> getAllDirectors() {
        String sqlQuery = "SELECT * FROM DIRECTORS";
        List<Director> directors = jdbcTemplate.query(sqlQuery, this::mapRowToDirector);
        log.debug("Получены режиссеры из базы данных {}", directors);
        return directors;
    }

    public void delete(long id) {
        String sqlQuery = "DELETE FROM FILMS_DIRECTORS WHERE DIRECTOR_ID = ?";
        jdbcTemplate.update(sqlQuery, id);
        sqlQuery = "DELETE FROM DIRECTORS WHERE DIRECTOR_ID = ?";
        jdbcTemplate.update(sqlQuery, id);
        log.debug("Удалён режиссер с id: {}", id);

    }

    public Director getById(long id) {
        String sqlQuery = "SELECT d.DIRECTOR_ID," +
                "                 d.DIRECTOR_NAME " +
                "FROM DIRECTORS AS d where DIRECTOR_ID = ?";
        int affected = jdbcTemplate.update("UPDATE DIRECTORS set DIRECTOR_ID = ? where DIRECTOR_ID = ?", id, id);
        if (affected == 0) {
            return null;
        }
        return jdbcTemplate.queryForObject(sqlQuery, this::mapRowToDirector, id);
    }

    public Director change(Director director) {
        String sqlQuery = "UPDATE DIRECTORS SET  DIRECTOR_NAME = ?  WHERE DIRECTOR_ID = ?";
        jdbcTemplate.update(sqlQuery,
                director.getName(),
                director.getId()
        );
        int affected = jdbcTemplate.update("UPDATE DIRECTORS set DIRECTOR_ID = ? where DIRECTOR_ID = ?", director.getId(), director.getId());
        if (affected == 0) {
            return null;
        }
        log.debug("данные режиссера {} обновлены в базе данных", director);
        return getById(director.getId());
    }

    public void setFilmDirector(Film film) {
        String sqlQuery = "SELECT D.DIRECTOR_ID," +
                "                 D.DIRECTOR_NAME " +
                "FROM FILMS_DIRECTORS AS FD " +
                "INNER JOIN DIRECTORS D on D.DIRECTOR_ID = FD.DIRECTOR_ID " +
                "WHERE FD.FILM_ID = ?";
        List<Director> directors = jdbcTemplate.query(sqlQuery, this::mapRowToDirector, film.getId());
        Set<Director> directorsSet = new HashSet<>(directors);
        film.setDirectors(directorsSet);
    }

    public Film updateFilmDirector(Film film) {
        jdbcTemplate.update("DELETE FROM FILMS_DIRECTORS WHERE FILM_ID = ?", film.getId());
        String sqlQuery = "INSERT INTO FILMS_DIRECTORS (FILM_ID, DIRECTOR_ID) VALUES ( ?, ?)";

        for (Director director : film.getDirectors()) {
            jdbcTemplate.update(sqlQuery,
                    film.getId(),
                    director.getId()
            );
        }
        log.debug("Режиссеры фильма {} обновлены в базе данных", film);
        return film;
    }

    public Set<Director> getFilmDirectors(Film film) {
        Set<Director> directors;
        String sqlQuery = "SELECT d.DIRECTOR_ID," +
                "                 d.DIRECTOR_NAME " +
                "FROM FILMS_DIRECTORS AS FD " +
                "INNER JOIN DIRECTORS D on D.DIRECTOR_ID = FD.DIRECTOR_ID " +
                "WHERE FD.FILM_ID = ?";
        directors = new HashSet<>(jdbcTemplate.query(sqlQuery, this::mapRowToDirector, film.getId()));
        return directors;
    }

    private Director mapRowToDirector(ResultSet resultSet, int i) throws SQLException {
        Director director = new Director();
        director.setId(resultSet.getLong("DIRECTOR_ID"));
        director.setName(resultSet.getString("DIRECTOR_NAME"));
        return director;
    }
}
