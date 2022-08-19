package ru.yandex.practicum.filmorate.storage;

public interface FilmLikeStorage {
    void addLike(long id, long userId);

    void deleteLike(long id, long userId);
}
