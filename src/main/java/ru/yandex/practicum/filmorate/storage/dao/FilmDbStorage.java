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
    private final String FILM_COLUMNS = "F.FILM_ID," +
            "                 F.NAME," +
            "                 F.DESCRIPTION," +
            "                 F.RELEASE_DATE," +
            "                 F.DURATION," +
            "                 F.MPA_ID," +
            "                 M.NAME," +
            "                 F.RATING ";

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
        String sqlQuery = "UPDATE FILMS SET  NAME = ?, DESCRIPTION = ?, RELEASE_DATE = ?, DURATION = ?," +
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
                "FROM FILMS AS F " +
                "INNER JOIN MPA M on M.MPA_ID = F.MPA_ID " +
                "GROUP BY F.FILM_ID";
        return jdbcTemplate.query(sqlQuery, this::mapRowToFilm);
    }

    @Override
    public Film getById(long id) {
        String sqlQuery = "SELECT " + FILM_COLUMNS +
                "FROM FILMS AS F " +
                "INNER JOIN MPA M on M.MPA_ID = F.MPA_ID AND F.FILM_ID = ?";

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
                "LEFT JOIN mpa AS m ON F.MPA_ID = M.MPA_ID " +
                "LEFT JOIN FILM_RATINGS AS FR on F.FILM_ID = FR.FILM_ID " +
                "WHERE FR.USER_ID IN (?, ?) AND FR.USER_RATING > 5 " +
                "GROUP BY FR.FILM_ID " +
                "HAVING COUNT(FR.USER_ID) = 2 " +
                "ORDER BY COUNT(FR.USER_ID) DESC";
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
                "FROM FILMS AS F " +
                "LEFT JOIN MPA AS M ON M.MPA_ID = F.FILM_ID " +
                "GROUP BY F.FILM_ID " +
                "ORDER BY F.RATING DESC " +
                "LIMIT ?;";
        return jdbcTemplate.query(sqlQuery, this::mapRowToFilm, count).stream();
    }

    private Stream<Film> getMostPopularFilmsByGenre(Integer count, Long genreId) {
        String sqlQuery = "SELECT " + FILM_COLUMNS +
                "FROM FILMS AS F " +
                "LEFT JOIN MPA AS M ON M.MPA_ID = F.FILM_ID " +
                "LEFT JOIN FILMS_GENRE AS G ON F.FILM_ID = G.FILM_ID " +
                "WHERE G.GENRE_ID = ? " +
                "GROUP BY F.FILM_ID " +
                "ORDER BY F.RATING DESC " +
                "LIMIT ?;";
        return jdbcTemplate.query(sqlQuery, this::mapRowToFilm, genreId, count).stream();
    }

    private Stream<Film> getMostPopularFilmsByYear(Integer count, Year year) {
        String sqlQuery = "SELECT " + FILM_COLUMNS +
                "FROM FILMS AS F " +
                "LEFT JOIN MPA AS M ON M.MPA_ID = F.FILM_ID " +
                "WHERE EXTRACT(YEAR FROM F.RELEASE_DATE) = ? " +
                "GROUP BY F.FILM_ID " +
                "ORDER BY F.RATING DESC " +
                "LIMIT ?;";
        return jdbcTemplate.query(sqlQuery, this::mapRowToFilm, year.getValue(), count).stream();
    }

    private Stream<Film> getMostPopularFilmsByGenreAndYear(Integer count, Long genreId, Year year) {
        String sqlQuery = "SELECT " + FILM_COLUMNS +
                "FROM FILMS AS F " +
                "LEFT JOIN MPA AS M ON M.MPA_ID = F.FILM_ID " +
                "LEFT JOIN FILMS_GENRE AS G ON F.FILM_ID = G.FILM_ID " +
                "WHERE G.GENRE_ID = ? " +
                "AND EXTRACT(YEAR FROM F.RELEASE_DATE) = ? " +
                "GROUP BY F.FILM_ID " +
                "ORDER BY F.RATING DESC " +
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
                "FROM FILMS AS F " +
                "LEFT JOIN MPA AS M ON M.MPA_ID = F.MPA_ID " +
                "LEFT JOIN FILMS_DIRECTORS ON FILMS_DIRECTORS.FILM_ID = F.FILM_ID " +
                "WHERE DIRECTOR_ID = ? " +
                "GROUP BY F.FILM_ID " +
                "ORDER BY F.RATING DESC ";
        return jdbcTemplate.query(selectMostPopularFilms, this::mapRowToFilm, id)
                .stream();
    }

    public Stream<Film> getSortFilmsDirectorByYear(final Long id) {
        final String selectMostPopularFilms = "SELECT " + FILM_COLUMNS +
                "FROM FILMS AS F " +
                "LEFT JOIN MPA AS M ON M.MPA_ID = F.MPA_ID " +
                "LEFT JOIN FILMS_DIRECTORS ON FILMS_DIRECTORS.FILM_ID = F.FILM_ID " +
                "WHERE DIRECTOR_ID = ? " +
                "GROUP BY F.FILM_ID " +
                "ORDER BY F.RELEASE_DATE ";
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
                "FROM FILMS AS F " +
                "LEFT JOIN MPA AS M ON M.MPA_ID = F.MPA_ID " +
                "WHERE F.NAME ILIKE ? " +
                "GROUP BY F.FILM_ID " +
                "ORDER BY F.RATING DESC;";
        return jdbcTemplate.query(sqlQuery, this::mapRowToFilm, "%" + query + "%")
                .stream();
    }

    private Stream<Film> searchFilmByDirector(String query) {
        String sqlQuery = "SELECT " + FILM_COLUMNS +
                "FROM FILMS AS F " +
                "LEFT JOIN MPA AS M ON M.MPA_ID = F.MPA_ID " +
                "LEFT JOIN FILMS_DIRECTORS AS FD ON F.FILM_ID = FD.FILM_ID " +
                "LEFT JOIN DIRECTORS AS D ON FD.DIRECTOR_ID = D.DIRECTOR_ID " +
                "WHERE D.DIRECTOR_NAME ILIKE ? " +
                "GROUP BY F.FILM_ID " +
                "ORDER BY F.RATING DESC;";
        return jdbcTemplate.query(sqlQuery, this::mapRowToFilm, "%" + query + "%")
                .stream();
    }

    private Stream<Film> searchFilmByNameOrDirector(String query) {
        String sqlQuery = "SELECT " + FILM_COLUMNS +
                "FROM FILMS AS F  " +
                "LEFT JOIN MPA AS M ON M.MPA_ID = F.MPA_ID  " +
                "LEFT JOIN FILMS_DIRECTORS AS FD ON F.FILM_ID = FD.FILM_ID  " +
                "LEFT JOIN DIRECTORS AS D ON FD.DIRECTOR_ID = D.DIRECTOR_ID  " +
                "WHERE D.DIRECTOR_NAME ILIKE ? OR F.NAME ILIKE ?  " +
                "GROUP BY F.FILM_ID " +
                "ORDER BY F.RATING DESC;";
        return jdbcTemplate.query(sqlQuery, this::mapRowToFilm, "%" + query + "%", "%" + query + "%")
                .stream();
    }
}
