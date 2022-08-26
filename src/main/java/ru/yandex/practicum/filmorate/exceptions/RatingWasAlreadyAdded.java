package ru.yandex.practicum.filmorate.exceptions;

public class RatingWasAlreadyAdded extends RuntimeException {

    public RatingWasAlreadyAdded(String message) {
        super(message);
    }
}
