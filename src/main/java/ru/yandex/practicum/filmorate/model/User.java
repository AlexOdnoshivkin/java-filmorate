package ru.yandex.practicum.filmorate.model;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Past;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
public class User implements IdControllable {
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

    private static long count;
    private long id;

   public void addToFriend(long id) {
       friends.add(id);
   }

   public void deleteFromFriends(long id) {
       friends.remove(id);
   }

    @Override
    public void generateId() {
        if(id == 0) {
            id = ++count;
        }
    }
}
