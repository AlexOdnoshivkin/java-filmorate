package ru.yandex.practicum.filmorate.storage.dao;

import lombok.AllArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.model.users.User;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
@AutoConfigureTestDatabase
@AllArgsConstructor(onConstructor_ = @Autowired)
public class UserDbStorageTest {
    private final UserDbStorage storage;

    @Test
    void userDbStorageTest() {
        User user = new User("test@gmail.com", "TestLogin", LocalDate.of(1993, 4, 20));
        user.setName("TestName");
        storage.add(user);
        User getUser = storage.getAll().get(0);

        assertThat(user.getName())
                .isEqualTo(getUser.getName());

        user.setName("update Name");
        storage.update(user);
        getUser = storage.getAll().get(0);
        assertThat("update Name")
                .isNotNull()
                .isEqualTo(getUser.getName());

        List<User> users = storage.getAll();
        assertThat(users.size())
                .isEqualTo(1);

        storage.delete(user);
        users = storage.getAll();
        assertThat(users.size())
                .isEqualTo(0);
        storage.delete(user);
    }
}