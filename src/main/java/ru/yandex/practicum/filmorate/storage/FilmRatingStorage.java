package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.films.Film;

public interface FilmRatingStorage {
    int addRating(long id, long userId, int rating);
    void deleteRating(long id, long userId);
    void CalculateFilmRating(Film film);
}
