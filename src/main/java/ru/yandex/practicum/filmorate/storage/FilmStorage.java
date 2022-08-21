package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.films.Film;

import java.time.Year;
import java.util.stream.Stream;

public interface FilmStorage extends EntityStorage<Film> {
    Stream<Film> getCommonFilms(Long userId, Long friendId);

    Stream<Film> getMostPopularFilmsDirector(final Long id);

    Stream<Film> getSortFilmsDirectorByYear(final Long id);

    Stream<Film> getMostPopularFilms(Integer count, Long genreId, Year year);
}
