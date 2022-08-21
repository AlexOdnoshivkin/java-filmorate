package ru.yandex.practicum.filmorate.service;

import java.time.Year;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.EntityNotFoundException;
import ru.yandex.practicum.filmorate.model.films.Director;
import ru.yandex.practicum.filmorate.model.films.Film;
import ru.yandex.practicum.filmorate.model.films.Genre;
import ru.yandex.practicum.filmorate.model.films.Mpa;
import ru.yandex.practicum.filmorate.storage.*;
import ru.yandex.practicum.filmorate.storage.FilmLikeStorage;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.GenreStorage;
import ru.yandex.practicum.filmorate.storage.dao.DirectorDBStorage;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@Slf4j
public class FilmService extends BaseService<Film> {

    private final UserService userService;
    private final FilmStorage storage;
    private final FilmLikeStorage filmLikeStorage;
    private final GenreStorage genreStorage;
    private final MpaStorage mpaStorage;
    private final EventService eventService;
    private final DirectorDBStorage directorDBStorage;

    @Autowired
    public FilmService(UserService userService, FilmStorage storage, FilmLikeStorage filmLikeStorage,
                       GenreStorage genreStorage, MpaStorage mpaStorage, EventService eventService,
                       DirectorDBStorage directorDBStorage) {
        super(storage);
        this.userService = userService;
        this.storage = storage;
        this.filmLikeStorage = filmLikeStorage;
        this.genreStorage = genreStorage;
        this.mpaStorage = mpaStorage;
        this.eventService = eventService;
        this.directorDBStorage = directorDBStorage;
    }

    @Override
    public List<Film> getAll() {
        List<Film> films = super.getAll();
        return films.stream()
                .peek(genreStorage::setFilmGenre)
                .peek(directorDBStorage::setFilmDirector)
                .collect(Collectors.toList());
    }

    @Override
    public Film getById(Long id) {
        Film film = super.getById(id);
        genreStorage.setFilmGenre(film);
        directorDBStorage.setFilmDirector(film);
        return film;
    }

    @Override
    public Film create(Film film) {
        film = storage.add(film);
        log.debug("Добавлен фильм: {}", film);
        if (film.getGenres() != null) {
            genreStorage.addFilmGenre(film);
        }
        if (film.getDirectors() != null) {
            directorDBStorage.addFilmDirector(film);
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
        directorDBStorage.updateFilmDirector(film);
        film = storage.update(film);
        film.setGenres(genreStorage.getFilmGenres(film));
        film.setDirectors(directorDBStorage.getFilmDirectors(film));
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
        eventService.addLikeEvent(userId, id);
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
        eventService.removeLikeEvent(userId, id);
    }

    public Stream<Film> getMostPopularFilms(Integer count, Long genreId, Year year) {
        return storage.getMostPopularFilms(count, genreId, year)
            .peek(genreStorage::setFilmGenre)
            .peek(directorDBStorage::setFilmDirector);
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

    public Stream<Film> getCommonFilms(Long userId, Long friendId) {
        return storage.getCommonFilms(userId, friendId);
    }

    public List<Film> getFilmsDirectorSort(long directorId, String sortBy) {
        Director director = directorDBStorage.getById(directorId);
        if (director == null) {
            throw new EntityNotFoundException("Такого режиссера нет.");
        }
        if (sortBy.equals("year")) {
            log.info("Получен запрос на получение фильмов режиссера {} отсортированных по году выпуска", directorId);
            return storage.getSortFilmsDirectorByYear(directorId)
                    .peek(genreStorage::setFilmGenre)
                    .peek(directorDBStorage::setFilmDirector)
                    .collect(Collectors.toList());
        } else if (sortBy.equals("likes")) {
            log.info("Получен запрос на получение фильмов режиссера {} отсортированных по лайкам", directorId);
            return storage.getMostPopularFilmsDirector(directorId)
                    .peek(genreStorage::setFilmGenre)
                    .peek(directorDBStorage::setFilmDirector)
                    .collect(Collectors.toList());
        }
        throw new EntityNotFoundException("Неверный параметр запроса.");
    }
}
