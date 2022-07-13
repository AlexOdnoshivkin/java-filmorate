package ru.yandex.practicum.filmorate.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import ru.yandex.practicum.filmorate.validation.ReleaseDate;


import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
@EqualsAndHashCode(callSuper = true)
public class Film extends BaseEntity {
    @NotEmpty (message = "Имя не может быть пустым")
    private final String name;
    @Size(max = 200, message = "Описание должно быть не более 200 символов")
    private final String description;
    @ReleaseDate("1895-12-27")
    private final LocalDate releaseDate;
    @Positive
    private final long duration;
    private final Set<Long> likes = new HashSet<>();

}
