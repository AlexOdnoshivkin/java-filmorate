package ru.yandex.practicum.filmorate.storage.dao;

import lombok.AllArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlGroup;
import ru.yandex.practicum.filmorate.model.films.Film;
import ru.yandex.practicum.filmorate.model.films.Mpa;

import java.time.LocalDate;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.AFTER_TEST_METHOD;
import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.BEFORE_TEST_METHOD;


@SpringBootTest
@AutoConfigureTestDatabase
@AllArgsConstructor(onConstructor_ = @Autowired)
class FilmDbStorageTest {
    private final FilmDbStorage storage;
    private final FilmRatingDbStorage filmRatingDbStorage;
    private final UserDbStorage userDbStorage;

    @Test
    void userDbStorageTest() {
        Film film1 = new Film("nisi eiusmod", "adipisicing", LocalDate.of(1967, 3, 24), 100);
        film1.setMpa(new Mpa(1, "G"));
        storage.add(film1);
        Film responseFilm = storage.getAll().get(0);
        assertThat(film1.getName())
                .isEqualTo(responseFilm.getName());

        film1 = new Film("UpdatedName", "adipisicing", LocalDate.of(1967, 3, 24), 100);
        film1.setMpa(new Mpa(1, "G"));
        film1.setId(responseFilm.getId());
        storage.update(film1);
        responseFilm = storage.getById(responseFilm.getId());
        assertThat("UpdatedName")
                .isEqualTo(responseFilm.getName());

        Film film2 = new Film("Other film", "description", LocalDate.of(1987, 4, 11), 120);
        film2.setMpa(new Mpa(1, "G"));
        storage.add(film2);

        List<Film> films = storage.getAll();
        assertThat(2)
                .isEqualTo(films.size());

        storage.delete(film2);
        films = storage.getAll();
        assertThat(1)
                .isEqualTo(films.size());

        storage.delete(film1);
        storage.delete(film2);
    }

    @Test
    @SqlGroup({
        @Sql(value = {"get-common-films.before.sql"}, executionPhase = BEFORE_TEST_METHOD),
        @Sql(value = {"get-common-films.after.sql"}, executionPhase = AFTER_TEST_METHOD)
    })
    void getCommonFilms() {

        Film trainspotting = new Film(
            "Trainspotting",
            "Absolutely amazing film!",
            LocalDate.of(1996, 2, 23),
            93);
        trainspotting.setId(1);
        trainspotting.setMpa(new Mpa(4L, "R"));

        Film bigFish = new Film(
            "Big Fish",
            "A brilliant experience",
            LocalDate.of(2003, 12, 25),
            125);
        bigFish.setId(2);
        bigFish.setMpa(new Mpa(2L, "PG"));


        filmRatingDbStorage.addRating(1, 1, 8);
        filmRatingDbStorage.addRating(1, 2, 7);
        filmRatingDbStorage.addRating(2, 1, 9);
        filmRatingDbStorage.addRating(2, 2, 3);

        Stream<Film> filmStream = storage.getCommonFilms(1L, 2L);

        assertThat(filmStream.collect(Collectors.toList()))
            .isNotEmpty()
            .containsExactly(trainspotting);
    }

    @Test
    @SqlGroup({
        @Sql(value = {"get-common-films-when-no-common-films-exist.before.sql"}, executionPhase = BEFORE_TEST_METHOD),
        @Sql(value = {"get-common-films-when-no-common-films-exist.after.sql"}, executionPhase = AFTER_TEST_METHOD)
    })
    void getCommonFilmsWhenNoCommonFilmsExist() {
        Stream<Film> filmStream = storage.getCommonFilms(1L, 2L);

        assertThat(filmStream).isEmpty();
    }
}