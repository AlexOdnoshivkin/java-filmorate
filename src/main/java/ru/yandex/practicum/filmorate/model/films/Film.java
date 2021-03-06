package ru.yandex.practicum.filmorate.model.films;

import lombok.Data;
import ru.yandex.practicum.filmorate.model.IdControllable;
import ru.yandex.practicum.filmorate.validation.ReleaseDate;


import javax.validation.constraints.NotEmpty;
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
    private Genre genre;
    private MPA mpa;

    public Genre getGenre() {
        return genre;
    }

    public void setGenre(Genre genre) {
        this.genre = genre;
    }

    public MPA getMpa() {
        return mpa;
    }

    public void setMpa(MPA mpa) {
        this.mpa = mpa;
    }

    @Override
    public void generateId() {
        if (id == 0) {
            id = ++count;
        }
    }

    public void addLike(long id) {
        likes.add(id);
    }

    public void deleteLike(long id) {
        likes.remove(id);
    }
}
