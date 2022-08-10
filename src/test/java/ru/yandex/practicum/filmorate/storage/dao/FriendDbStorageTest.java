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
class FriendDbStorageTest {
    private final FriendDbStorage storage;
    private final UserDbStorage userDbStorage;

    @Test
    void addFriend() {
        User user1 = new User("test@gmail.com", "TestLogin", LocalDate.of(1993, 4, 20));
        user1.setName("TestName");
        User user2 = new User("test@gmail.com", "TestLogin", LocalDate.of(1993, 4, 20));
        user2.setName("TestName");
        userDbStorage.add(user1);
        userDbStorage.add(user2);
        List<User> users = userDbStorage.getAll();
        storage.addFriend(users.get(0).getId(), users.get(1).getId());
        List<User> friends = storage.getAllFriends(users.get(0).getId());

        assertThat(1)
                .isEqualTo(friends.size());

        User user3 = new User("test@gmail.com", "TestLogin", LocalDate.of(1993, 4, 20));
        user3.setName("TestName");
        userDbStorage.add(user3);
        users = userDbStorage.getAll();
        storage.addFriend(users.get(0).getId(), users.get(2).getId());
        storage.addFriend(users.get(1).getId(), users.get(2).getId());

        List<User> commonFriends = storage.getCommonFriends(users.get(0).getId(), users.get(2).getId());

        assertThat(1)
                .isEqualTo(commonFriends.size());

        storage.deleteFromFriends(users.get(0).getId(), users.get(1).getId());

        friends = storage.getAllFriends(users.get(0).getId());

        assertThat(1)
                .isEqualTo(friends.size());

        storage.deleteFromFriends(users.get(0).getId(),users.get(2).getId());
        storage.deleteFromFriends(users.get(1).getId(),users.get(2).getId());
        userDbStorage.delete(user1);
        userDbStorage.delete(user2);
        userDbStorage.delete(user3);
    }
}