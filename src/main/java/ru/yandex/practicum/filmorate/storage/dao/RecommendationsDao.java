package ru.yandex.practicum.filmorate.storage.dao;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.storage.RecommendationsStorage;

import java.util.ArrayList;
import java.util.List;

@Repository
@Slf4j
@AllArgsConstructor
public class RecommendationsDao implements RecommendationsStorage {
    JdbcTemplate jdbcTemplate;

    @Override
    public List<Long> getRecommendations(long userId) {
        List<Long> commonUser = getCommonUser(userId);
        return getCommonFilm(commonUser, userId);
    }

    private List<Long> getCommonUser(long userId) {
        String sqlQuery = "SELECT LI.USER_ID FROm FILM_RATINGS AS LI " +
                "INNER JOIN FILM_RATINGS AS l ON l.FILM_ID = LI.FILM_ID " +
                "AND NOT l.USER_ID = LI.USER_ID AND l.USER_ID = ? " +
                "WHERE (LI.USER_RATING > 5 AND L.USER_RATING > 5) OR (LI.USER_RATING < 6 AND L.USER_RATING < 6) " +
                "GROUP BY LI.USER_ID;";
        return jdbcTemplate.queryForList(sqlQuery, Long.class, userId);
    }

    private List<Long> getCommonFilm(List<Long> commonUser, long userId) {
        List<Long> resultFilm = new ArrayList<>();
        String sqlQuery = "SELECT f.FILM_ID " +
                "FROM FILMS AS F " +
                "JOIN FILM_RATINGS AS L ON F.FILM_ID = L.FILM_ID and USER_ID= ? " +
                "WHERE USER_RATING > 5 " +
                "EXCEPT " +
                "SELECT f.FILM_ID " +
                "FROM FILMS AS f " +
                "JOIN  FILM_RATINGS AS l on f.FILM_ID = l.FILM_ID and USER_ID = ?";
        for (Long id : commonUser) {
            resultFilm.addAll(jdbcTemplate.queryForList(sqlQuery, Long.class, id, userId));
        }
        return resultFilm;
    }
}
