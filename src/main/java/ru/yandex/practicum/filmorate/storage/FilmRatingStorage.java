package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.films.Film;

public interface FilmRatingStorage {
    void addRating(long id, long userId, int rating);
    void deleteRating(long id, long userId);
    void updateRating(long id, long userId, int rating);
    void getFilmRating(Film film);
}
