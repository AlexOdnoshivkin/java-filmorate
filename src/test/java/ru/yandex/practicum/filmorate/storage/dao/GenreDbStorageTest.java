package ru.yandex.practicum.filmorate.storage.dao;

import lombok.AllArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.model.films.Genre;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
@AutoConfigureTestDatabase
@AllArgsConstructor(onConstructor_ = @Autowired)
class GenreDbStorageTest {
    private final GenreDbStorage storage;

    @Test
    void getAllGenres() {
        List<Genre> genres = storage.getAllGenres();

        assertThat(genres.size())
                .isEqualTo(6);
    }

    @Test
    void getGenreById() {
        Genre genre = storage.getGenreById(1);
        Genre trueGenre = new Genre(1, "Комедия");

        assertThat(genre)
                .isEqualTo(trueGenre);
    }
}