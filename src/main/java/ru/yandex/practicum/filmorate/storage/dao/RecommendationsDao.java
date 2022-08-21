package ru.yandex.practicum.filmorate.storage.dao;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
@Slf4j
@AllArgsConstructor
public class RecommendationsDao {
    JdbcTemplate jdbcTemplate;

    public List<Long> getRecommendations(long userId) {
        List<Long> commonUser = getCommonUser(userId);
        return getCommonFilm(commonUser, userId);
    }

    private List<Long> getCommonUser(long userId) {
        String sqlQuery = "SELECT LI.USER_ID FROM LIKES AS LI " +
                "JOIN LIKES AS l ON l.FILM_ID = LI.FILM_ID " +
                "AND NOT l.USER_ID = LI.USER_ID AND l.USER_ID = ? " +
                "group by LI.USER_id;";
        return jdbcTemplate.queryForList(sqlQuery, Long.class, userId);
    }

    private List<Long> getCommonFilm(List<Long> commonUser, long userId) {
        List<Long> resultFilm = new ArrayList<>();
        String sqlQuery = "SELECT f.FILM_ID " +
                "FROM FILMS AS F " +
                "JOIN LIKES AS l ON F.FILM_ID = l.FILM_ID and USER_ID= ? " +
                "EXCEPT " +
                "SELECT f.FILM_ID " +
                "FROM FILMS AS f " +
                "JOIN  LIKES AS l on f.FILM_ID = l.FILM_ID and USER_ID = ?";
        for (Long id : commonUser) {
            resultFilm.addAll(jdbcTemplate.queryForList(sqlQuery, Long.class, id, userId));
        }
        return resultFilm;
    }
}
