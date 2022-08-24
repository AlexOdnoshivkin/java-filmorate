package ru.yandex.practicum.filmorate.service;

import java.time.Year;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.EntityNotFoundException;
import ru.yandex.practicum.filmorate.model.films.Director;
import ru.yandex.practicum.filmorate.model.films.Film;
import ru.yandex.practicum.filmorate.model.films.Genre;
import ru.yandex.practicum.filmorate.model.films.Mpa;
import ru.yandex.practicum.filmorate.storage.*;
import ru.yandex.practicum.filmorate.storage.FilmRatingStorage;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.GenreStorage;
import ru.yandex.practicum.filmorate.storage.dao.DirectorDBStorage;
import ru.yandex.practicum.filmorate.storage.dao.RecommendationsDao;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
@Slf4j
public class FilmService {

    private final UserService userService;
    private final FilmStorage storage;
    private final FilmRatingStorage filmRatingStorage;
    private final GenreStorage genreStorage;
    private final MpaStorage mpaStorage;
    private final EventService eventService;
    private final DirectorDBStorage directorDBStorage;
    private final RecommendationsDao recommendationsDao;


    public List<Film> getAll() {
        List<Film> films = storage.getAll();
        return films.stream()
                .peek(genreStorage::setFilmGenre)
                .peek(directorDBStorage::setFilmDirector)
                .peek(filmRatingStorage::CalculateFilmRating)
                .collect(Collectors.toList());
    }

    public Film getById(Long id) {
        if (storage.getById(id) == null) {
            throw new EntityNotFoundException("Объект не найден");
        }
        Film film = storage.getById(id);
        genreStorage.setFilmGenre(film);
        directorDBStorage.setFilmDirector(film);
        filmRatingStorage.CalculateFilmRating(film);
        return film;
    }

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
        filmRatingStorage.CalculateFilmRating(film);
        return film;
    }

    public void addRating(long id, long userId, int rating) {
        if (userService.storage.getById(userId) == null) {
            throw new EntityNotFoundException("Пользователь не найден");
        }
        if (storage.getById(id) == null) {
            throw new EntityNotFoundException("Фильм не найден");
        }
        if (filmRatingStorage.addRating(id, userId, rating) == 1) {
            eventService.addRatingEvent(userId, id);
        } else {
            eventService.updateRatingEvent(userId, id);
        }

    }

    public void delete(Long id) {
        Film film = storage.getById(id);
        if (film == null) {
            throw new EntityNotFoundException("Фильм не найден");
        }
        storage.delete(film);
        log.debug("Удалён фильм: {}", film);
    }

    public void deleteRating(long id, long userId) {
        if (userService.storage.getById(userId) == null) {
            throw new EntityNotFoundException("Пользователь не найден");
        }
        if (storage.getById(id) == null) {
            throw new EntityNotFoundException("Фильм не найден");
        }
        filmRatingStorage.deleteRating(id, userId);
        eventService.removeRatingEvent(userId, id);
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
        return storage.getCommonFilms(userId, friendId)
                .peek(filmRatingStorage::CalculateFilmRating); //записываем рейтинг фильма
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
                    .peek(filmRatingStorage::CalculateFilmRating)
                    .collect(Collectors.toList());
        } else if (sortBy.equals("likes")) {
            log.info("Получен запрос на получение фильмов режиссера {} отсортированных по лайкам", directorId);
            return storage.getMostPopularFilmsDirector(directorId)
                    .peek(genreStorage::setFilmGenre)
                    .peek(directorDBStorage::setFilmDirector)
                    .peek(filmRatingStorage::CalculateFilmRating)
                    .collect(Collectors.toList());
        }
        throw new EntityNotFoundException("Неверный параметр запроса.");
    }

    public Stream<Film> searchFilm(String query, String by) {
        return storage.search(query, by)
                .peek(genreStorage::setFilmGenre)
                .peek(directorDBStorage::setFilmDirector)
                .peek(filmRatingStorage::CalculateFilmRating);
    }

    public List<Film> getRecommendations(long userId) {
        return recommendationsDao.getRecommendations(userId).stream()
                .map(this::getById)
                .peek(filmRatingStorage::CalculateFilmRating)
                .collect(Collectors.toList());
    }

}
