package ru.yandex.practicum.filmorate.model.films;

import lombok.Data;
import lombok.RequiredArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@RequiredArgsConstructor
public class Review {
    private Long reviewId;
    private Integer useful = 0;
    @NotBlank
    private final String content;
    @NotNull
    private final Boolean isPositive;
    @NotNull
    private final Long userId;
    @NotNull
    private final Long filmId;
}
