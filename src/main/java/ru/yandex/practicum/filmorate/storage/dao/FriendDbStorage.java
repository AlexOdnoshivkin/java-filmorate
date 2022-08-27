package ru.yandex.practicum.filmorate.storage.dao;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.users.User;
import ru.yandex.practicum.filmorate.storage.FriendsStorage;
import ru.yandex.practicum.filmorate.storage.map.MapRawToUser;

import java.util.List;

@Repository
@AllArgsConstructor
@Slf4j
public class FriendDbStorage implements FriendsStorage, MapRawToUser {
    private final JdbcTemplate jdbcTemplate;

    public void addFriend(long reqUserId, long respUserId) {
        // Проверяем, был ли уже отправлен запрос на дружбу
        int affected = jdbcTemplate.update("UPDATE FRIENDS set REQ_USER_ID = ?, RESP_USER_ID = ? " +
                "where REQ_USER_ID = ? AND RESP_USER_ID = ?", respUserId, reqUserId, respUserId, reqUserId);
        System.out.println(affected);

        // Если нет, добавляем строку в таблицу
        String sqlQuery;
        if (affected == 0) {
            sqlQuery = "INSERT INTO FRIENDS (REQ_USER_ID, RESP_USER_ID) VALUES (?, ?)";
            // Устанавливаем флаг подтверждения
        } else {
            sqlQuery = "UPDATE friends SET IS_FRIEND = 1 WHERE REQ_USER_ID = ? AND RESP_USER_ID = ?";
        }
        jdbcTemplate.update(sqlQuery, reqUserId, respUserId);
    }

    public List<User> getAllFriends(long id) {
        String sqlQuery = "SELECT DISTINCT U.USER_ID, " +
                "                          U.NAME," +
                "                          U.LOGIN," +
                "                          U.EMAIL," +
                "                          U.BIRTHDAY " +
                "FROM FRIENDS AS F " +
                "         INNER JOIN USERS AS U ON (U.USER_ID = F.REQ_USER_ID" +
                "    OR U.USER_ID = F.RESP_USER_ID) " +
                "    AND (F.REQ_USER_ID = ?" +
                "        OR F.RESP_USER_ID = ?)" +
                "WHERE ((F.RESP_USER_ID = ?" +
                "  AND F.IS_FRIEND) OR F.REQ_USER_ID = ?) AND U.USER_ID != ?" +
                "ORDER BY U.USER_ID";
        List<User> users = jdbcTemplate.query(sqlQuery, this::mapRowToUser, id, id, id, id, id);
        log.debug("Выгружен список друзей пользователя с id {}, количество друзей: {}", id, users.size());
        return users;
    }

    public List<User> getCommonFriends(long id, long otherId) {
        String sqlQuery = "SELECT DISTINCT U.USER_ID," +
                "                          U.NAME," +
                "                          U.EMAIL," +
                "                          U.login," +
                "                          U.birthday " +
                "FROM FRIENDS AS F" +
                "         INNER JOIN USERS AS U ON (U.USER_ID = F.REQ_USER_ID" +
                "    OR U.USER_ID = F.RESP_USER_ID)" +
                "    AND (F.REQ_USER_ID = ?" +
                "        OR F.RESP_USER_ID = ?" +
                "        OR F.REQ_USER_ID = ?" +
                "        OR F.RESP_USER_ID = ?)" +
                "WHERE U.USER_ID != ?" +
                "  AND U.USER_ID != ?" +
                "GROUP BY U.USER_ID " +
                "ORDER BY U.USER_ID";
        List<User> users = jdbcTemplate.query(sqlQuery, this::mapRowToUser, id, id, otherId, otherId, id, otherId);
        log.debug("Выгружен список общих друзей для пользователей с id {} и {}, количестов друзей: {}",
                id, otherId, users.size());
        return users;
    }

    @Override
    public void deleteFromFriends(long id, long otherId) {
        String sqlQuery = "DELETE FROM FRIENDS WHERE (REQ_USER_ID = ? AND RESP_USER_ID = ?) OR " +
                "(REQ_USER_ID = ? AND RESP_USER_ID = ?)";
        jdbcTemplate.update(sqlQuery, id, otherId, otherId, id);
        log.debug("Пользователь с id {} удалён из друзей пользователя с id {}", id, otherId);
    }
}
