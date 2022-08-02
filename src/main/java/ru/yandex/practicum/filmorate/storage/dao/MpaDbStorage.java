package ru.yandex.practicum.filmorate.storage.dao;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.films.Mpa;
import ru.yandex.practicum.filmorate.storage.MpaStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
@Slf4j
@AllArgsConstructor
public class MpaDbStorage implements MpaStorage {

    private JdbcTemplate jdbcTemplate;

    @Override
    public List<Mpa> getAllMpa() {
        String sqlQuery = "SELECT * FROM MPA";
        List<Mpa> mpas = jdbcTemplate.query(sqlQuery, this::mapRowToMpa);
        log.debug("Получены рейтинги из базы данных {}", mpas);
        return mpas;
    }

    @Override
    public Mpa getMpaById(long id) {
        int affected = jdbcTemplate.update("UPDATE MPA set MPA_ID = ? where MPA_ID = ?", id, id);
        if (affected == 0) {
            return null;
        }
        String sqlQuery = "SELECT * FROM MPA WHERE MPA_ID = ?";
        return jdbcTemplate.queryForObject(sqlQuery, this::mapRowToMpa, id);
    }

    private Mpa mapRowToMpa(ResultSet resultSet, int id) throws SQLException {
        log.debug("Получен рейтинг c id {} из базы данных", id);
        return new Mpa(resultSet.getLong("mpa_id"), resultSet.getString("name"));
    }
}
