package ru.yandex.practicum.filmorate.model.films;

import lombok.Data;
import ru.yandex.practicum.filmorate.model.IdControllable;
import ru.yandex.practicum.filmorate.validation.ReleaseDate;


import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
public class Film implements IdControllable {
    @NotEmpty(message = "Имя не может быть пустым")
    private final String name;
    @Size(max = 200, message = "Описание должно быть не более 200 символов")
    private final String description;
    @ReleaseDate("1895-12-27")
    private final LocalDate releaseDate;
    @Positive
    private final long duration;
    private final Set<Long> likes = new HashSet<>();
    private static long count;
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

    @Override
    public void generateId() {
        if (id == 0) {
            id = ++count;
        }
    }
}
