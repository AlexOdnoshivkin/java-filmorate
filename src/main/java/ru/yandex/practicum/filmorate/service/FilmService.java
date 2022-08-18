package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.EntityNotFoundException;
import ru.yandex.practicum.filmorate.model.films.Film;
import ru.yandex.practicum.filmorate.model.films.Genre;
import ru.yandex.practicum.filmorate.model.films.Mpa;
import ru.yandex.practicum.filmorate.storage.EntityStorage;
import ru.yandex.practicum.filmorate.storage.FilmLikeStorage;
import ru.yandex.practicum.filmorate.storage.GenreStorage;
import ru.yandex.practicum.filmorate.storage.MpaStorage;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class FilmService extends BaseService<Film> {

    private final UserService userService;
    private final EntityStorage<Film> storage;
    private final FilmLikeStorage filmLikeStorage;
    private final GenreStorage genreStorage;
    private final MpaStorage mpaStorage;

    @Autowired
    public FilmService(UserService userService, EntityStorage<Film> storage, FilmLikeStorage filmLikeStorage,
                       GenreStorage genreStorage, MpaStorage mpaStorage) {
        super(storage);
        this.userService = userService;
        this.storage = storage;
        this.filmLikeStorage = filmLikeStorage;
        this.genreStorage = genreStorage;
        this.mpaStorage = mpaStorage;
    }

    @Override
    public List<Film> getAll() {
        List<Film> films = super.getAll();
        return films.stream()
                .peek(genreStorage::setFilmGenre)
                .collect(Collectors.toList());
    }

    @Override
    public Film getById(Long id) {
        Film film = super.getById(id);
        genreStorage.setFilmGenre(film);
        return film;
    }

    @Override
    public Film create(Film film) {
        film = storage.add(film);
        log.debug("Добавлен фильм: {}", film);
        if (film.getGenres() != null) {
            genreStorage.addFilmGenre(film);
        }
        return film;
    }

    @Override
    public Film update(Film film) {
        if (storage.getById(film.getId()) != null) {
            log.debug("Обновлён фильм: {}", film);

        } else {
            throw new EntityNotFoundException("Фильм не найден");
        }
        genreStorage.updateFilmGenre(film);
        film = storage.update(film);
        film.setGenres(genreStorage.getFilmGenres(film));
        return film;
    }

    public void addLike(long id, long userId) {
        if (userService.storage.getById(userId) == null) {
            throw new EntityNotFoundException("Пользователь не найден");
        }
        if (storage.getById(id) == null) {
            throw new EntityNotFoundException("Фильм не найден");
        }
        filmLikeStorage.addLike(id, userId);
    }

    @Override
    public void delete(Long id) {
        Film film = storage.getById(id);
        if (film == null) {
            throw new EntityNotFoundException("Фильм не найден");
        }
        storage.delete(film);
        log.debug("Удалён фильм: {}", film);
    }

    public void deleteLike(long id, long userId) {
        if (userService.storage.getById(userId) == null) {
            throw new EntityNotFoundException("Пользователь не найден");
        }
        Film film = storage.getById(id);
        if (storage.getById(id) == null) {
            throw new EntityNotFoundException("Фильм не найден");
        }
        filmLikeStorage.deleteLike(id, userId);
    }

    public List<Film> getMostPopularFilms(int count) {
        List<Long> mostPopularFilmsId = filmLikeStorage.getMostPopularFilmsId(count);
        return mostPopularFilmsId.stream()
                .map(Long -> getById(Long))
                .collect(Collectors.toList());
    }

    public List<Genre> getAllGenres() {
        return genreStorage.getAllGenres();
    }

    public Genre getGenreById(long id) {
        if (genreStorage.getGenreById(id) == null) {
            throw new EntityNotFoundException("Жанр не найден");
        }
        return genreStorage.getGenreById(id);
    }

    public List<Mpa> getAllMpa() {
        return mpaStorage.getAllMpa();
    }

    public Mpa getMpaById(long id) {
        if (mpaStorage.getMpaById(id) == null) {
            throw new EntityNotFoundException("Рейтинг не найден");
        }
        return mpaStorage.getMpaById(id);
    }
}
