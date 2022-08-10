package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.films.Film;

import java.util.List;

public interface FilmLikeStorage {
    public void addLike(long id, long userId);

    public void deleteLike(long id, long userId);

    public List<Long> getMostPopularFilmsId(int count);
}
