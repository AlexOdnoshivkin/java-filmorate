package ru.yandex.practicum.filmorate.storage.dao;

import lombok.AllArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.model.films.Film;
import ru.yandex.practicum.filmorate.model.films.Mpa;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;


@SpringBootTest
@AutoConfigureTestDatabase
@AllArgsConstructor(onConstructor_ = @Autowired)
class FilmDbStorageTest {
    private final FilmDbStorage storage;

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

}