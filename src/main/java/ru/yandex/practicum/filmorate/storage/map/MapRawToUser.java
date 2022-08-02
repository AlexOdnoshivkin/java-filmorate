package ru.yandex.practicum.filmorate.storage.map;

import ru.yandex.practicum.filmorate.model.users.User;

import java.sql.ResultSet;
import java.sql.SQLException;

public interface MapRawToUser {
    default User mapRowToUser(ResultSet resultSet, int id) throws SQLException {
        User user = new User(
                resultSet.getString("email"),
                resultSet.getString("login"),
                resultSet.getDate("birthday").toLocalDate()
        );
        user.setId(resultSet.getLong("user_id"));
        user.setName(resultSet.getString("name"));
        return user;
    }
}
