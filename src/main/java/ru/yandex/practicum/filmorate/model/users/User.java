package ru.yandex.practicum.filmorate.model.users;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Past;
import java.time.LocalDate;

@Data
@EqualsAndHashCode(of="id")
public class User {
    @NotEmpty
    @Email(message = "Некорректный формат email")
    private final String email;
    @NotEmpty
    @NotBlank
    private final String login;
    private String name = null;
    @Past
    private final LocalDate birthday;

    private static long count;
    private long id;
}
