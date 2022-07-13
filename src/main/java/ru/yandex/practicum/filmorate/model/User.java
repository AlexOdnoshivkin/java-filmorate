package ru.yandex.practicum.filmorate.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Past;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
@EqualsAndHashCode(callSuper = true)
public class User extends BaseEntity {
    @NotEmpty
    @Email(message = "Некорректный формат email")
    private final String email;
    @NotEmpty
    @NotBlank
    private final String login;
    private String name = null;
    @Past
    private final LocalDate birthday;
    private final Set<Long> friends = new HashSet<>();

   public void addToFriend(long id) {
       friends.add(id);
   }

   public void deleteFromFriends(long id) {
       friends.remove(id);
   }

}
