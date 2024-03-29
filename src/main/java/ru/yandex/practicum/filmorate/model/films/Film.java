package ru.yandex.practicum.filmorate.model.films;

import lombok.Data;
import lombok.EqualsAndHashCode;
import ru.yandex.practicum.filmorate.validation.ReleaseDate;


import javax.validation.constraints.*;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
@EqualsAndHashCode(of="id")
public class Film {
    @NotBlank(message = "Имя не может быть пустым")
    private final String name;
    @Size(max = 200, message = "Описание должно быть не более 200 символов")
    private final String description;
    @ReleaseDate("1895-12-27")
    private final LocalDate releaseDate;
    @Positive
    private final long duration;
    private final Set<Long> likes = new HashSet<>();
    private long id;
    private Set<Genre> genres = new HashSet<>();
    private Set<Director> directors = new HashSet<>();
    @NotNull
    private Mpa mpa;

    public Set<Genre> getGenres() {
        return genres;
    }

    public Mpa getMpa() {
        return mpa;
    }

    public void setMpa(Mpa mpa) {
        this.mpa = mpa;
    }
}
