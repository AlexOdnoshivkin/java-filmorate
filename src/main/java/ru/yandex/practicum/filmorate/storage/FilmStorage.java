package ru.yandex.practicum.filmorate.storage;

import java.util.stream.Stream;
import ru.yandex.practicum.filmorate.model.films.Film;

public interface FilmStorage extends EntityStorage<Film> {
  Stream<Film> getCommonFilms(Long userId, Long friendId);
}
