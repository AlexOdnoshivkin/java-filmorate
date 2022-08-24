package ru.yandex.practicum.filmorate.model.films;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class Director {
    private long id;
    @NotBlank
    private String name;
}
