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
        int affected = jdbcTemplate.update("UPDATE friends set req_user_id = ?, resp_user_id = ? " +
                "where req_user_id = ? AND resp_user_id = ?", respUserId, reqUserId, respUserId, reqUserId);
        System.out.println(affected);

        // Если нет, добавляем строку в таблицу
        String sqlQuery;
        if (affected == 0) {
            sqlQuery = "INSERT INTO friends (req_user_id, resp_user_id) VALUES (?, ?)";
            // Устанавливаем флаг подтверждения
        } else {
            sqlQuery = "UPDATE friends SET IS_FRIEND = 1 WHERE REQ_USER_ID = ? AND RESP_USER_ID = ?";
        }
        jdbcTemplate.update(sqlQuery, reqUserId, respUserId);
    }

    public List<User> getAllFriends(long id) {
        String sqlQuery = "SELECT DISTINCT u.user_id,\n" +
                "                u.name,\n" +
                "                u.LOGIN,\n" +
                "                u.email,\n" +
                "                u.birthday\n" +
                "FROM friends AS f\n" +
                "         INNER JOIN users AS u ON (u.user_id = f.REQ_USER_ID\n" +
                "    OR u.user_id = f.RESP_USER_ID)\n" +
                "    AND (f.REQ_USER_ID = ?\n" +
                "        OR f.RESP_USER_ID = ?)\n" +
                "WHERE ((f.RESP_USER_ID = ?\n" +
                "  AND f.is_friend) OR f.REQ_USER_ID = ?) AND u.USER_ID != ?\n" +
                "ORDER BY u.user_id";
        List<User> users = jdbcTemplate.query(sqlQuery, this::mapRowToUser, id, id, id, id, id);
        log.debug("Выгружен список друзей пользователя с id {}, количество друзей: {}", id, users.size());
        return users;
    }

    public List<User> getCommonFriends(long id, long otherId) {
        String sqlQuery = "SELECT DISTINCT u.user_id,\n" +
                "                u.name,\n" +
                "                u.email,\n" +
                "                u.login,\n" +
                "                u.birthday\n" +
                "FROM friends AS f\n" +
                "         INNER JOIN users AS u ON (u.user_id = f.REQ_USER_ID\n" +
                "    OR u.user_id = f.RESP_USER_ID)\n" +
                "    AND (f.REQ_USER_ID = ?\n" +
                "        OR f.RESP_USER_ID = ?\n" +
                "        OR f.REQ_USER_ID = ?\n" +
                "        OR f.RESP_USER_ID = ?)\n" +
                "WHERE u.user_id != ?\n" +
                "  AND u.user_id != ?\n" +
                "GROUP BY u.user_id\n" +
                "ORDER BY u.user_id";
        List<User> users = jdbcTemplate.query(sqlQuery, this::mapRowToUser, id, id, otherId, otherId, id, otherId);
        log.debug("Выгружен список общих друзей для пользователей с id {} и {}, количестов друзей: {}", id, otherId, users.size());
        return users;
    }

    @Override
    public void deleteFromFriends(long id, long otherId) {
        String sqlQuery = "DELETE FROM FRIENDS WHERE (REQ_USER_ID = ? AND RESP_USER_ID = ?) OR " +
                "(REQ_USER_ID = ? AND RESP_USER_ID = ?)";
        jdbcTemplate.update(sqlQuery, id, otherId, otherId, id);
        log.debug("Пользователь с id ? удалён из друзей пользователя с id ?", id, otherId);
    }
}
