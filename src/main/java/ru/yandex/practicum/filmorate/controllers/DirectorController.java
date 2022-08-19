package ru.yandex.practicum.filmorate.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.films.Director;
import ru.yandex.practicum.filmorate.service.DirectorService;

import javax.validation.Valid;
import java.util.Collection;

@RestController
@Slf4j
public class DirectorController {
    private final DirectorService directorService;

    @Autowired
    public DirectorController(DirectorService directorService) {
        this.directorService = directorService;
    }

    @GetMapping("/directors")
    public Collection<Director> getDirectors() {
        log.info("Получен запрос на получение списка всех режиссеров");
        return directorService.getAll();
    }

    @GetMapping("/directors/{id}")
    public Director getDirectorById(@PathVariable long id) {
        log.info("Получен запрос на получение режиссера с id: {}", id);
        return directorService.getById(id);
    }

    @PostMapping("/directors")
    public Director postDirector(@Valid @RequestBody Director director) {
        return directorService.create(director);
    }

    @PutMapping("/directors")
    public Director putDirector(@Valid @RequestBody Director director) {
        return directorService.update(director);
    }

    @DeleteMapping("/directors/{id}")
    public void deleteFromFriends(@PathVariable long id) {
        log.info("Получен запрос на удаление режиссера с id {}", id);
        directorService.deleteFromDirector(id);
    }
}
