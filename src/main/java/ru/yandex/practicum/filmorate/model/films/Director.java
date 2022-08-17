package ru.yandex.practicum.filmorate.model.films;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;

@Data
public class Director {
    private long id;
    @NotEmpty
    @NotBlank
    private String name;
}
