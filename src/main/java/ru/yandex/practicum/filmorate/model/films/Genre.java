package ru.yandex.practicum.filmorate.model.films;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(of="id")
public class Genre {
    private final long id;
    private final String name;
}
