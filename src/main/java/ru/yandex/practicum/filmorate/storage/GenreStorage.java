package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.films.Film;
import ru.yandex.practicum.filmorate.model.films.Genre;

import java.util.List;
import java.util.Set;

public interface GenreStorage {
    public List<Genre> getAllGenres();

    public Genre getGenreById(long id);

    public Film updateFilmGenre(Film film);

    public void addFilmGenre(Film film);

    public void setFilmGenre(Film films);

    Set<Genre> getFilmGenres(Film film);
}
