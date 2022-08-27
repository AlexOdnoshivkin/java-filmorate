package ru.yandex.practicum.filmorate.storage.dao;

import java.time.Year;

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
    private final String FILM_COLUMNS = "f.FILM_ID," +
            "                 f.NAME," +
            "                 f.DESCRIPTION," +
            "                 f.RELEASE_DATE," +
            "                 f.DURATION," +
            "                 f.MPA_ID," +
            "                 m.NAME," +
            "                 f.rating ";

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
        String sqlQuery = "SELECT " + FILM_COLUMNS +
                "FROM FILMS AS f " +
                "INNER JOIN MPA m on m.MPA_ID = f.MPA_ID " +
                "GROUP BY f.FILM_ID";
        return jdbcTemplate.query(sqlQuery, this::mapRowToFilm);
    }

    @Override
    public Film getById(long id) {
        String sqlQuery = "SELECT " + FILM_COLUMNS +
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
        String selectCommonFilms = "SELECT " + FILM_COLUMNS +

                "FROM films AS f " +
                "LEFT JOIN mpa AS m ON f.mpa_id = m.mpa_id " +
                "LEFT JOIN FILM_RATINGS AS fr on f.film_id = fr.film_id " +
                "WHERE fr.user_id IN (?, ?) AND fr.USER_RATING > 5 " +
                "GROUP BY fr.film_id " +
                "HAVING COUNT(fr.user_id) = 2 " +
                "ORDER BY COUNT(fr.user_id) DESC";
        return jdbcTemplate
                .query(selectCommonFilms, this::mapRowToFilm, userId, friendId)
                .stream();
    }

    @Override
    public Stream<Film> getMostPopularFilms(Integer count, Long genreId, Year year) {
        Stream<Film> popularFilms;

        if (genreId == null) {
            if (year == null) popularFilms = getMostPopularFilmsDefault(count);
            else popularFilms = getMostPopularFilmsByYear(count, year);
        } else {
            if (year == null) popularFilms = getMostPopularFilmsByGenre(count, genreId);
            else popularFilms = getMostPopularFilmsByGenreAndYear(count, genreId, year);
        }

        return popularFilms;
    }

    private Stream<Film> getMostPopularFilmsDefault(Integer count) {
        String sqlQuery = "SELECT " + FILM_COLUMNS +
                "FROM films AS f " +
                "LEFT JOIN mpa AS m ON m.mpa_id = f.film_id " +
                "GROUP BY f.film_id " +
                "ORDER BY f.RATING DESC " +
                "LIMIT ?;";
        return jdbcTemplate.query(sqlQuery, this::mapRowToFilm, count).stream();
    }

    private Stream<Film> getMostPopularFilmsByGenre(Integer count, Long genreId) {
        String sqlQuery = "SELECT " + FILM_COLUMNS +
                "FROM films AS f " +
                "LEFT JOIN mpa AS m ON m.mpa_id = f.film_id " +
                "LEFT JOIN films_genre AS g ON f.film_id = g.film_id " +
                "WHERE g.genre_id = ? " +
                "GROUP BY f.film_id " +
                "ORDER BY f.RATING DESC " +
                "LIMIT ?;";
        return jdbcTemplate.query(sqlQuery, this::mapRowToFilm, genreId, count).stream();
    }

    private Stream<Film> getMostPopularFilmsByYear(Integer count, Year year) {
        String sqlQuery = "SELECT " + FILM_COLUMNS +
                "FROM films AS f " +
                "LEFT JOIN mpa AS m ON m.mpa_id = f.film_id " +
                "WHERE EXTRACT(YEAR FROM f.release_date) = ? " +
                "GROUP BY f.film_id " +
                "ORDER BY f.RATING DESC " +
                "LIMIT ?;";
        return jdbcTemplate.query(sqlQuery, this::mapRowToFilm, year.getValue(), count).stream();
    }

    private Stream<Film> getMostPopularFilmsByGenreAndYear(Integer count, Long genreId, Year year) {
        String sqlQuery = "SELECT " + FILM_COLUMNS +
                "FROM films AS f " +
                "LEFT JOIN mpa AS m ON m.mpa_id = f.film_id " +
                "LEFT JOIN films_genre AS g ON f.film_id = g.film_id " +
                "WHERE g.genre_id = ? " +
                "AND EXTRACT(YEAR FROM f.release_date) = ? " +
                "GROUP BY f.film_id " +
                "ORDER BY f.RATING DESC " +
                "LIMIT ?;";
        return jdbcTemplate.query(sqlQuery, this::mapRowToFilm, genreId, year.getValue(), count).stream();
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
        film.setRating(resultSet.getFloat("rating"));
        return film;
    }

    public Stream<Film> getMostPopularFilmsDirector(final Long id) {
        final String selectMostPopularFilms = "SELECT " + FILM_COLUMNS +
                "FROM films AS f " +
                "LEFT JOIN mpa AS m ON m.mpa_id = f.mpa_id " +
                "LEFT JOIN films_directors ON films_directors.film_id = f.film_id " +
                "WHERE director_id = ? " +
                "GROUP BY f.film_id " +
                "ORDER BY f.RATING DESC ";
        return jdbcTemplate.query(selectMostPopularFilms, this::mapRowToFilm, id)
                .stream();
    }

    public Stream<Film> getSortFilmsDirectorByYear(final Long id) {
        final String selectMostPopularFilms = "SELECT " + FILM_COLUMNS +
                "FROM films AS f " +
                "LEFT JOIN mpa AS m ON m.mpa_id = f.mpa_id " +
                "LEFT JOIN films_directors ON films_directors.film_id = f.film_id " +
                "WHERE director_id = ? " +
                "GROUP BY f.film_id " +
                "ORDER BY f.RELEASE_DATE ";
        return jdbcTemplate.query(selectMostPopularFilms, this::mapRowToFilm, id)
                .stream();
    }

    public Stream<Film> search(String query, String by) {
        boolean byDirector = by.contains("director");
        boolean byTitle = by.contains("title");
        Stream<Film> results;

        if (byDirector) {
            if (byTitle) results = searchFilmByNameOrDirector(query);
            else results = searchFilmByDirector(query);
        } else {
            if (byTitle) results = searchFilmByName(query);
            else results = Stream.empty();
        }

        return results;
    }

    private Stream<Film> searchFilmByName(String query) {
        String sqlQuery = "SELECT " + FILM_COLUMNS +
                "FROM films AS f " +
                "LEFT JOIN mpa AS m ON m.mpa_id = f.mpa_id " +
                "WHERE f.name ILIKE ? " +
                "GROUP BY f.film_id " +
                "ORDER BY f.RATING DESC;";
        return jdbcTemplate.query(sqlQuery, this::mapRowToFilm, "%" + query + "%")
                .stream();
    }

    private Stream<Film> searchFilmByDirector(String query) {
        String sqlQuery = "SELECT " + FILM_COLUMNS +
                "FROM films AS f " +
                "LEFT JOIN mpa AS m ON m.mpa_id = f.mpa_id " +
                "LEFT JOIN films_directors AS fd ON f.film_id = fd.film_id " +
                "LEFT JOIN directors AS d ON fd.director_id = d.director_id " +
                "WHERE d.director_name ILIKE ? " +
                "GROUP BY f.film_id " +
                "ORDER BY f.RATING DESC;";
        return jdbcTemplate.query(sqlQuery, this::mapRowToFilm, "%" + query + "%")
                .stream();
    }

    private Stream<Film> searchFilmByNameOrDirector(String query) {
        String sqlQuery = "SELECT " + FILM_COLUMNS +
                "FROM films AS f  " +
                "LEFT JOIN mpa AS m ON m.mpa_id = f.mpa_id  " +
                "LEFT JOIN films_directors AS fd ON f.film_id = fd.film_id  " +
                "LEFT JOIN directors AS d ON fd.director_id = d.director_id  " +
                "WHERE d.director_name ILIKE ? OR f.name ILIKE ?  " +
                "GROUP BY f.film_id " +
                "ORDER BY f.RATING DESC;";
        return jdbcTemplate.query(sqlQuery, this::mapRowToFilm, "%" + query + "%", "%" + query + "%")
                .stream();
    }
}
