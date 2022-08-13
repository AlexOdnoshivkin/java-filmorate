package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.films.Film;

import java.util.List;

public interface FilmLikeStorage {
    void addLike(long id, long userId);

    void deleteLike(long id, long userId);

    List<Long> getMostPopularFilmsId(int count);
}
