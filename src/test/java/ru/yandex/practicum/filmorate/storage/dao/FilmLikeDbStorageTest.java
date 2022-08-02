package ru.yandex.practicum.filmorate.storage.dao;

import lombok.AllArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.model.films.Film;
import ru.yandex.practicum.filmorate.model.films.Mpa;
import ru.yandex.practicum.filmorate.model.users.User;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;


@SpringBootTest
@AutoConfigureTestDatabase
@AllArgsConstructor(onConstructor_ = @Autowired)
class FilmLikeDbStorageTest {
    private final FilmLikeDbStorage storage;
    private final FilmDbStorage filmDbStorage;

    private final UserDbStorage userDbStorage;

    @Test
    void filmLikeDbStorageTest() {
        Film film1 = new Film("nisi eiusmod", "adipisicing", LocalDate.of(1967, 3, 24), 100);
        film1.setMpa(new Mpa(1, "G"));
        Film film2 = new Film("Other film", "description", LocalDate.of(1987, 4, 11), 120);
        film2.setMpa(new Mpa(1, "G"));
        filmDbStorage.add(film1);
        filmDbStorage.add(film2);
        User user1 = new User("test@gmail.com", "TestLogin", LocalDate.of(1993, 4, 20));
        user1.setName("TestName");
        User user2 = new User("test@gmail.com", "TestLogin", LocalDate.of(1993, 4, 20));
        user2.setName("TestName");
        userDbStorage.add(user1);
        userDbStorage.add(user2);
        List<User> users = userDbStorage.getAll();
        List<Film> films = filmDbStorage.getAll();
        storage.addLike(films.get(0).getId(),users.get(0).getId());
        storage.addLike(films.get(1).getId(),users.get(0).getId());
        storage.addLike(films.get(1).getId(),users.get(1).getId());

        List<Long> popularFilm = storage.getMostPopularFilmsId(10);
        assertThat(films.get(1).getId())
                .isEqualTo(popularFilm.get(0));
        assertThat(films.get(0).getId())
                .isEqualTo(popularFilm.get(1));

        storage.deleteLike(films.get(1).getId(),users.get(0).getId());
        storage.deleteLike(films.get(1).getId(),users.get(1).getId());
        popularFilm = storage.getMostPopularFilmsId(10);
        assertThat(films.get(0).getId())
                .isEqualTo(popularFilm.get(0));
        storage.deleteLike(films.get(0).getId(),users.get(0).getId());
        userDbStorage.delete(user1);
        userDbStorage.delete(user2);
        filmDbStorage.delete(film1);
        filmDbStorage.delete(film2);
    }

}