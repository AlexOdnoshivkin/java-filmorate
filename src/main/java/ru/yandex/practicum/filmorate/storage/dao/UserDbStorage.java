package ru.yandex.practicum.filmorate.storage.dao;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.users.User;
import ru.yandex.practicum.filmorate.storage.map.MapRawToUser;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.sql.*;
import java.util.List;

@Repository
@Primary
@AllArgsConstructor
@Slf4j
public class UserDbStorage implements UserStorage, MapRawToUser {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public User add(User user) {
        String sqlQuery = "INSERT INTO USERS(NAME, LOGIN, EMAIL, BIRTHDAY) values ( ?, ?, ?, ? ) ";
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement(sqlQuery, new String[]{"USER_ID"});
            stmt.setString(1, user.getName());
            stmt.setString(2, user.getLogin());
            stmt.setString(3, user.getEmail());
            stmt.setDate(4, Date.valueOf(user.getBirthday()));
            return stmt;
        }, keyHolder);
        user.setId(keyHolder.getKey().longValue());
        log.debug("Пользователь {} записан в базу данных", user);
        return user;
    }

    @Override
    public User update(User user) {
        String sqlQuery = "UPDATE USERS SET  name = ?, login = ?, email = ?, birthday = ? WHERE USER_ID = ?";

        jdbcTemplate.update(sqlQuery,
                user.getName(),
                user.getLogin(),
                user.getEmail(),
                user.getBirthday(),
                user.getId());
        log.debug("данные пользователя {} обновлены в базе данных", user);
        return getById(user.getId());
    }

    @Override
    public void delete(User user) {
        String sqlQuery = "DELETE FROM USERS WHERE user_id = ?";
        jdbcTemplate.update(sqlQuery, user.getId());
    }

    @Override
    public List<User> getAll() {
        String sqlQuery = "SELECT user_id, name, login, email, birthday FROM users";
        return jdbcTemplate.query(sqlQuery, this::mapRowToUser);
    }

    @Override
    public User getById(long id) {
        String sqlQuery = "SELECT user_id, name, login, email, birthday FROM users WHERE user_id = ?";
        int affected = jdbcTemplate.update("UPDATE users set user_id = ? where user_id = ?", id, id);
        if (affected == 0) {
            return null;
        }
        return jdbcTemplate.queryForObject(sqlQuery, this::mapRowToUser, id);
    }
}

