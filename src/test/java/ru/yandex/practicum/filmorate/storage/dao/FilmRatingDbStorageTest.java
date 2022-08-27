package ru.yandex.practicum.filmorate.storage.dao;

import java.time.Year;
import java.util.stream.Collectors;

import lombok.AllArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlGroup;
import ru.yandex.practicum.filmorate.model.films.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.AFTER_TEST_METHOD;
import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.BEFORE_TEST_METHOD;

@SpringBootTest
@AutoConfigureTestDatabase
@AllArgsConstructor(onConstructor_ = @Autowired)
class FilmRatingDbStorageTest {
    private final FilmRatingDbStorage storage;
    private final FilmService filmDbStorage;

    @Test
    @SqlGroup({
            @Sql(value = {"get-common-films.before.sql"}, executionPhase = BEFORE_TEST_METHOD),
            @Sql(value = {"get-common-films.after.sql"}, executionPhase = AFTER_TEST_METHOD)
    })
    void commonFilms() {
        storage.addRating(1, 1, 5);
        storage.addRating(2, 1, 6);
        storage.addRating(2, 2, 7);

        List<Film> commonFilms = filmDbStorage.getCommonFilms(1L, 2L).collect(Collectors.toList());

        assertThat(commonFilms.get(0))
                .isEqualTo(filmDbStorage.getById(2L));
        assertThat(commonFilms.size())
                .isEqualTo(1);
    }

    @Test
    @SqlGroup({
            @Sql(value = {"get-common-films.before.sql"}, executionPhase = BEFORE_TEST_METHOD),
            @Sql(value = {"get-common-films.after.sql"}, executionPhase = AFTER_TEST_METHOD)
    })
    void getMostPopularFilmsByDefault() {
        storage.saveRating(1, 1, 5);
        storage.saveRating(2, 2, 6);
        storage.saveRating(3, 3, 7);

        List<Film> films = filmDbStorage.getAll();

        List<Film> popularFilm = filmDbStorage.getMostPopularFilms(10, null, null)
                .collect(Collectors.toList());
        assertThat(films.get(2).getId())
                .isEqualTo(popularFilm.get(0).getId());
        assertThat(films.get(2))
                .isEqualTo(popularFilm.get(0));
    }

    @Test
    @SqlGroup({
            @Sql(value = {"get-common-films.before.sql"}, executionPhase = BEFORE_TEST_METHOD),
            @Sql(value = {"get-common-films.after.sql"}, executionPhase = AFTER_TEST_METHOD)
    })
    void getMostPopularFilmsByYear() {
        storage.addRating(1, 1, 5);
        storage.addRating(2, 2, 6);
        storage.addRating(3, 3, 7);

        List<Film> films = filmDbStorage.getAll();

        List<Film> popularFilm = filmDbStorage.getMostPopularFilms(10, null, Year.of(1996))
                .collect(Collectors.toList());
        assertThat(films.get(0).getId())
                .isEqualTo(popularFilm.get(0).getId());
        assertThat(films.get(0))
                .isEqualTo(popularFilm.get(0));
    }

    @Test
    @SqlGroup({
            @Sql(value = {"get-common-films.before.sql"}, executionPhase = BEFORE_TEST_METHOD),
            @Sql(value = {"get-common-films.after.sql"}, executionPhase = AFTER_TEST_METHOD)
    })
    void getMostPopularFilmsByName() {
        storage.addRating(1, 1, 5);
        storage.addRating(2, 2, 6);
        storage.addRating(3, 3, 7);

        List<Film> films = filmDbStorage.getAll();

        List<Film> popularFilm = filmDbStorage.searchFilm("Sleep", "title")
                .collect(Collectors.toList());
        assertThat(films.get(2).getId())
                .isEqualTo(popularFilm.get(0).getId());
        assertThat(films.get(2))
                .isEqualTo(popularFilm.get(0));
    }

    @Test
    @SqlGroup({
            @Sql(value = {"get-common-films.before.sql"}, executionPhase = BEFORE_TEST_METHOD),
            @Sql(value = {"get-common-films.after.sql"}, executionPhase = AFTER_TEST_METHOD)
    })
    void getMostPopularFilmsByGenres() {
        storage.addRating(1, 1, 5);
        storage.addRating(2, 2, 6);
        storage.addRating(3, 3, 7);

        List<Film> films = filmDbStorage.getAll();

        List<Film> popularFilm = filmDbStorage.getMostPopularFilms(10, 1L, null)
                .collect(Collectors.toList());
        assertThat(films.get(2).getId())
                .isEqualTo(popularFilm.get(0).getId());
        assertThat(films.get(2))
                .isEqualTo(popularFilm.get(0));
    }

    @Test
    @SqlGroup({
            @Sql(value = {"get-common-films.before.sql"}, executionPhase = BEFORE_TEST_METHOD),
            @Sql(value = {"get-common-films.after.sql"}, executionPhase = AFTER_TEST_METHOD)
    })
    void getMostPopularFilmsDirectorByYear() {
        storage.addRating(1, 1, 5);
        storage.addRating(2, 2, 6);
        storage.addRating(3, 3, 7);

        List<Film> films = filmDbStorage.getAll();

        List<Film> popularFilm = filmDbStorage.getFilmsDirectorSort(1, "year");
        assertThat(films.get(0).getId())
                .isEqualTo(popularFilm.get(0).getId());
        assertThat(films.get(0))
                .isEqualTo(popularFilm.get(0));
    }

    @Test
    @SqlGroup({
            @Sql(value = {"get-common-films.before.sql"}, executionPhase = BEFORE_TEST_METHOD),
            @Sql(value = {"get-common-films.after.sql"}, executionPhase = AFTER_TEST_METHOD)
    })
    void getMostPopularFilmsDirectorByRating() {
        storage.addRating(1, 1, 5);
        storage.addRating(2, 2, 6);
        storage.addRating(3, 3, 7);

        List<Film> films = filmDbStorage.getAll();

        List<Film> popularFilm = filmDbStorage.getFilmsDirectorSort(2, "rating");
        assertThat(films.get(1).getId())
                .isEqualTo(popularFilm.get(0).getId());
        assertThat(films.get(1))
                .isEqualTo(popularFilm.get(0));
    }
}