package ru.yandex.practicum.filmorate.controllers;

import java.time.Year;
import javax.validation.constraints.Min;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.films.Film;
import ru.yandex.practicum.filmorate.model.films.Genre;
import ru.yandex.practicum.filmorate.model.films.Mpa;
import ru.yandex.practicum.filmorate.service.FilmService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import java.util.stream.Collectors;
import java.util.*;

@RestController
@Slf4j
public class FilmController {
    private final FilmService filmService;

    @Autowired
    public FilmController(FilmService filmService) {
        this.filmService = filmService;
    }

    @GetMapping("/films/search")
    public List<Film> searchFilm(@RequestParam String query, @RequestParam String by) {
        log.info("Получен запрос на поиск фильмов c запросом {} по параметрам {}", query, by);
        return filmService.searchFilm(query, by).collect(Collectors.toList());
    }

    @GetMapping("/films")
    public List<Film> getFilms() {
        log.info("Получен запрос на получение списка всех фильмов");
        return filmService.getAll();
    }

    @GetMapping("/films/{id}")
    public Film getFilmById(@PathVariable long id) {
        log.info("Получен запрос на получение фильма с id: {}", id);
        return filmService.getById(id);
    }

    @GetMapping("/films/popular")
    public List<Film> getMostPopularFilms(
            @RequestParam(defaultValue = "10") @Min(1) Integer count,
            @RequestParam(required = false) Long genreId,
            @RequestParam(required = false) Year year
    ) {
        log.info("Получен запрос на получение {} самых популярных фильмов: год = {}, жанр id = {}", count, genreId, year);
        return filmService.getMostPopularFilms(count, genreId, year).collect(Collectors.toList());
    }

    @GetMapping("/films/director/{directorId}")
    public List<Film> getFilmsDirectorSort(@RequestParam String sortBy, @PathVariable long directorId) {
        return filmService.getFilmsDirectorSort(directorId, sortBy);
    }


    @GetMapping("/genres")
    public List<Genre> getAllGenres() {
        log.info("Получен запрос на получение списка всех жанров");
        return filmService.getAllGenres();
    }

    @GetMapping("/genres/{id}")
    public Genre getGenreById(@PathVariable long id) {

        log.info("Получен запрос на получение жанра с id {}", id);
        return filmService.getGenreById(id);
    }

    @GetMapping("/mpa")
    public List<Mpa> getAllMpa() {
        log.info("Получен запрос на получение списка всех рейтингов");
        return filmService.getAllMpa();
    }

    @GetMapping("/mpa/{id}")
    public Mpa getMpaById(@PathVariable long id) {
        log.info("Получен запрос на получение жанра с id {}", id);
        return filmService.getMpaById(id);
    }

    @PostMapping("/films")
    public Film postFilm(@Valid @RequestBody Film film) {
        return filmService.create(film);
    }

    @PutMapping("/films")
    public Film putFilm(@Valid @RequestBody Film film) {
        return filmService.update(film);
    }

    @PutMapping("/films/{id}/like/{userId}")
    public void putLike(@PathVariable long id, @PathVariable long userId) {
        log.info("Получен запрос на добавление лайка фильму с id: {} пользователем с id: {}", id, userId);
        filmService.addLike(id, userId);
    }

    @DeleteMapping("/films/{id}/like/{userId}")
    public void deleteLike(@PathVariable long id, @PathVariable long userId) {
        log.info("Получен запрос на удаление лайка фильму с id: {} пользователем с id: {}", id, userId);
        filmService.deleteLike(id, userId);
    }

    @GetMapping("/films/common")
    public List<Film> getCommonFilms(@RequestParam Long userId, @RequestParam Long friendId) {
        log.info("Получен запрос на получение списка общих фильмов для пользователей с id {}, {}", userId, friendId);
        return filmService.getCommonFilms(userId, friendId).collect(Collectors.toList());
    }

    @DeleteMapping("/films/{filmId}")
    public void deleteFilm(@PathVariable long filmId) {
        log.info("Получен запрос на удаление фильма с id: {}.", filmId);
        filmService.delete(filmId);
    }

    @GetMapping("/users/{userId}/recommendations")
    public List<Film> getRecommendations(@PathVariable @Positive long userId) {
        log.info("Получен запрос рекомендация для пользователя с id: {}.", userId);
        return filmService.getRecommendations(userId);
    }
}