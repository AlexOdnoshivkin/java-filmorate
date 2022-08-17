package ru.yandex.practicum.filmorate.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.EntityNotFoundException;
import ru.yandex.practicum.filmorate.model.films.Review;
import ru.yandex.practicum.filmorate.storage.dao.FilmDbStorage;
import ru.yandex.practicum.filmorate.storage.dao.ReviewDbStorage;
import ru.yandex.practicum.filmorate.storage.dao.UserDbStorage;

import java.util.List;

@Service
@AllArgsConstructor
public class ReviewService {

    ReviewDbStorage reviewDbStorage;
    FilmDbStorage filmDbStorage;
    UserDbStorage userDbStorage;

    public Review getById(Long id) {
        Review review = reviewDbStorage.getById(id);
        if (review == null) {
            throw new EntityNotFoundException("Отзыв не найден!");
        }
        return review;
    }

    public List<Review> getAll(Long filmId, int count) {
        if (filmId == 0 || !(filmDbStorage.getById(filmId) == null)) {
            return reviewDbStorage.getAll(filmId, count);
        } else {
            throw new EntityNotFoundException("Фильм не найден!");
        }
    }

    public Review create(Review review) {
        if (review.getUserId() == null || review.getUserId() <= 0) {
            throw new EntityNotFoundException("Пользователь не найден!");
        } else if (review.getFilmId() == null || review.getFilmId() <= 0) {
            throw new EntityNotFoundException("Фильм не найден!");
        }
        review.setUseful(0);
        return reviewDbStorage.create(review);
    }

    public Review update(Review review) {
        return reviewDbStorage.update(review);
    }

    public void deleteById(Long id) {
        if (reviewDbStorage.getById(id) == null) {
            throw new EntityNotFoundException("Отзыв не найден!");
        }
        reviewDbStorage.deleteById(id);
    }

    public void addReactionReview(Long idReview, Long idUser, int i) {
        if (userDbStorage.getById(idUser) == null || idUser <= 0) {
            throw new EntityNotFoundException("Пользователь не найден!");
        } else if (reviewDbStorage.getById(idReview) == null || idReview <= 0) {
            throw new EntityNotFoundException("Отзыв не найден!");
        }
        reviewDbStorage.addReactionReview(idReview, idUser, i);
    }

    public void deleteReactionReview(Long idReview, Long idUser) {
        if (userDbStorage.getById(idUser) == null || idUser <= 0) {
            throw new EntityNotFoundException("Пользователь не найден!");
        } else if (reviewDbStorage.getById(idReview) == null || idReview <= 0) {
            throw new EntityNotFoundException("Отзыв не найден!");
        }
        reviewDbStorage.deleteReactionReview(idReview, idUser);
    }

}
