package ru.yandex.practicum.filmorate.storage;

public interface FilmRatingStorage {
    void addRating(long id, long userId, float rating);

    void deleteRating(long id, long userId);
}
