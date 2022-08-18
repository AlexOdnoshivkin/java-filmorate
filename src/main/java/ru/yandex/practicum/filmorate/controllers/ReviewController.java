package ru.yandex.practicum.filmorate.controllers;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.films.Review;
import ru.yandex.practicum.filmorate.service.ReviewService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import java.util.List;

@RestController
@AllArgsConstructor
@Slf4j
@RequestMapping("/reviews")
public class ReviewController {
    ReviewService reviewService;

    @GetMapping("{id}")
    public Review getReviewById(@PathVariable @Positive Long id) {
        log.info("Получен запрос на получение отзыва с id: {}", id);
        return reviewService.getById(id);
    }

    @GetMapping
    public List<Review> getAll(@RequestParam(defaultValue = "0", required = false) Long filmId,
                               @RequestParam(defaultValue = "10", required = false) int count) {
        log.info("Получен запрос на получение всех отзывов");
        return reviewService.getAll(filmId, count);
    }

    @PostMapping
    public Review create(@RequestBody @Valid Review review) {
        return reviewService.create(review);
    }

    @PutMapping
    public Review update(@RequestBody @Valid Review review) {
        return reviewService.update(review);
    }

    @DeleteMapping("{id}")
    public void deleteReview(@PathVariable @Positive Long id) {
        log.info("Получен запрос на удаление отзыва с id: {}", id);
        reviewService.deleteById(id);
    }

    @PutMapping("{id}/like/{userId}")
    public void addLikeReview(@PathVariable @Positive Long id, @PathVariable @Positive Long userId) {
        log.info("Получен запрос на добавление лайка для отзыва с id: {} пользователем с id {}", id, userId);
        reviewService.addReactionReview(id, userId, 1);
    }

    @PutMapping("{id}/dislike/{userId}")
    public void addDislikeReview(@PathVariable @Positive Long id, @PathVariable @Positive Long userId) {
        log.info("Получен запрос на добавление дизлайка для отзыва с id: {} пользователем с id {}", id, userId);
        reviewService.addReactionReview(id, userId, -1);
    }

    @DeleteMapping("{id}/like/{userId}")
    public void deleteLikeReview(@PathVariable @Positive Long id, @PathVariable @Positive Long userId) {
        log.info("Получен запрос на удаление лайка для отзыва с id: {} пользователем с id {}", id, userId);
        reviewService.deleteReactionReview(id, userId);
    }

    @DeleteMapping("{id}/dislike/{userId}")
    public void deleteDislikeReview(@PathVariable @Positive Long id, @PathVariable @Positive Long userId) {
        log.info("Получен запрос на удаление дизлайка для отзыва с id: {} пользователем с id {}", id, userId);
        reviewService.deleteReactionReview(id, userId);
    }
}
