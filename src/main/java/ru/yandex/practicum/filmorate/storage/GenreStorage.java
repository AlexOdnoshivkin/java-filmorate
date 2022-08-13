package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.films.Film;
import ru.yandex.practicum.filmorate.model.films.Genre;

import java.util.List;
import java.util.Set;

public interface GenreStorage {
    List<Genre> getAllGenres();

    Genre getGenreById(long id);

    Film updateFilmGenre(Film film);

    void addFilmGenre(Film film);

    void setFilmGenre(Film films);

    Set<Genre> getFilmGenres(Film film);
}
