package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;
import java.util.Map;

public interface FilmStorage {

    public void addFilm(Film film);

    public void updateFilm(Film film);

    public void deleteFilm(Film film);

    public List<Film> getAllFilms();

    public Film getFilmById(long id);
}
