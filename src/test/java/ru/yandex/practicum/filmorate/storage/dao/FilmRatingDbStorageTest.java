package ru.yandex.practicum.filmorate.storage.dao;

import java.time.Year;
import java.util.stream.Collectors;

import lombok.AllArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.model.films.Film;
import ru.yandex.practicum.filmorate.model.films.Mpa;
import ru.yandex.practicum.filmorate.model.users.User;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
@AutoConfigureTestDatabase
@AllArgsConstructor(onConstructor_ = @Autowired)
class FilmRatingDbStorageTest {
    private final FilmRatingDbStorage storage;
    private final FilmService filmDbStorage;
    private final UserDbStorage userDbStorage;

    @Test
    void commonFilms() {
        Film film1 = new Film("nisi eiusmod", "adipisicing", LocalDate.of(1967, 3, 24), 100);
        film1.setMpa(new Mpa(1, "G"));
        Film film2 = new Film("Other film", "description", LocalDate.of(1987, 4, 11), 120);
        film2.setMpa(new Mpa(1, "G"));
        filmDbStorage.create(film1);
        filmDbStorage.create(film2);
        User user1 = new User("test@gmail.com", "TestLogin", LocalDate.of(1993, 4, 20));
        user1.setName("TestName");
        User user2 = new User("test@gmail.com", "TestLogin", LocalDate.of(1993, 4, 20));
        user2.setName("TestName");
        userDbStorage.add(user1);
        userDbStorage.add(user2);
        List<User> users = userDbStorage.getAll();

        storage.addRating(film1.getId(), users.get(0).getId(), 5);
        storage.addRating(film2.getId(), users.get(0).getId(), 6);
        storage.addRating(film2.getId(), users.get(1).getId(), 7);

        List<Film> films = filmDbStorage.getAll();

        List<Film> commonFilms = filmDbStorage.getCommonFilms(1L, 2L).collect(Collectors.toList());

        assertThat(commonFilms.get(0))
                .isEqualTo(films.get(1));

        assertThat(commonFilms.size())
                .isEqualTo(1);
    }

    @Test
    void getMostPopularFilmsByDefault() {
        Film film1 = new Film("nisi eiusmod", "adipisicing", LocalDate.of(1996, 3, 24), 100);
        film1.setMpa(new Mpa(1, "G"));

        Film film2 = new Film("Other film", "description", LocalDate.of(1987, 4, 11), 120);
        film2.setMpa(new Mpa(1, "G"));

        Film film3 = new Film("film", "descrip", LocalDate.of(1965, 5, 11), 110);
        film3.setMpa(new Mpa(1, "G"));

        filmDbStorage.create(film1);
        filmDbStorage.create(film2);
        filmDbStorage.create(film3);

        User user1 = new User("test@gmail.com", "TestLogin", LocalDate.of(1993, 4, 20));
        user1.setName("TestName");
        User user2 = new User("test@gmail.com", "TestLogin", LocalDate.of(1993, 4, 20));
        user2.setName("TestName");

        userDbStorage.add(user1);
        userDbStorage.add(user2);

        List<User> users = userDbStorage.getAll();

        storage.addRating(film1.getId(), users.get(0).getId(), 5);
        storage.addRating(film2.getId(), users.get(0).getId(), 6);
        storage.addRating(film3.getId(), users.get(1).getId(), 7);

        List<Film> films = filmDbStorage.getAll();

        List<Film> popularFilm = filmDbStorage.getMostPopularFilms(10, null, null)
                .collect(Collectors.toList());
        assertThat(films.get(2).getId())
                .isEqualTo(popularFilm.get(0).getId());
        assertThat(films.get(2))
                .isEqualTo(popularFilm.get(0));
    }
    @Test
    void getMostPopularFilmsByYear() {
        Film film1 = new Film("nisi eiusmod", "adipisicing", LocalDate.of(1996, 3, 24), 100);
        film1.setMpa(new Mpa(1, "G"));

        Film film2 = new Film("Other film", "description", LocalDate.of(1987, 4, 11), 120);
        film2.setMpa(new Mpa(1, "G"));

        Film film3 = new Film("film", "descrip", LocalDate.of(1965, 5, 11), 110);
        film3.setMpa(new Mpa(1, "G"));

        filmDbStorage.create(film1);
        filmDbStorage.create(film2);
        filmDbStorage.create(film3);

        User user1 = new User("test@gmail.com", "TestLogin", LocalDate.of(1993, 4, 20));
        user1.setName("TestName");
        User user2 = new User("test@gmail.com", "TestLogin", LocalDate.of(1993, 4, 20));
        user2.setName("TestName");

        userDbStorage.add(user1);
        userDbStorage.add(user2);

        List<User> users = userDbStorage.getAll();

        storage.addRating(film1.getId(), users.get(0).getId(), 5);
        storage.addRating(film2.getId(), users.get(0).getId(), 6);
        storage.addRating(film3.getId(), users.get(1).getId(), 7);

        List<Film> films = filmDbStorage.getAll();

        List<Film> popularFilm = filmDbStorage.getMostPopularFilms(10, null, Year.of(1996))
                .collect(Collectors.toList());
        assertThat(films.get(0).getId())
                .isEqualTo(popularFilm.get(0).getId());
        assertThat(films.get(0))
                .isEqualTo(popularFilm.get(0));
    }

    @Test
    void getMostPopularFilmsByName() {
        Film film1 = new Film("nisi eiusmod", "adipisicing", LocalDate.of(1996, 3, 24), 100);
        film1.setMpa(new Mpa(1, "G"));

        Film film2 = new Film("Other film", "description", LocalDate.of(1987, 4, 11), 120);
        film2.setMpa(new Mpa(1, "G"));

        filmDbStorage.create(film1);
        filmDbStorage.create(film2);

        User user1 = new User("test@gmail.com", "TestLogin", LocalDate.of(1993, 4, 20));
        user1.setName("TestName");
        User user2 = new User("test@gmail.com", "TestLogin", LocalDate.of(1993, 4, 20));
        user2.setName("TestName");

        userDbStorage.add(user1);
        userDbStorage.add(user2);

        List<User> users = userDbStorage.getAll();

        storage.addRating(film1.getId(), users.get(0).getId(), 5);
        storage.addRating(film2.getId(), users.get(0).getId(), 6);
        storage.addRating(film2.getId(), users.get(1).getId(), 7);

        List<Film> films = filmDbStorage.getAll();

        List<Film> popularFilm = filmDbStorage.searchFilm("er f", "title")
                .collect(Collectors.toList());
        assertThat(films.get(1).getId())
                .isEqualTo(popularFilm.get(0).getId());
        assertThat(films.get(1))
                .isEqualTo(popularFilm.get(0));
    }
}