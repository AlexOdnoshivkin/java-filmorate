package ru.yandex.practicum.filmorate.storage.dao;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.films.Review;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
@AllArgsConstructor
@Slf4j
public class ReviewDbStorage {

    private final JdbcTemplate jdbcTemplate;

    final static String sql = "SELECT r.*, " +
            "COALESCE(u,0) AS USEFUL " +
            "FROM REVIEWS AS r " +
            "LEFT JOIN (SELECT REVIEW_ID,SUM(UTILITY) as u " +
            "FROM REVIEW_LIKE GROUP BY REVIEW_ID ) AS d " + "ON r.REVIEW_ID = d.REVIEW_ID ";

    public List<Review> getAll(Long filmId, int count) {
        String sqlQuery = "";
        if (filmId == 0) {
            sqlQuery = sql +
                    "ORDER BY USEFUL desc " +
                    "LIMIT ?";
        } else {
            sqlQuery = sql +
                    "WHERE FILM_ID = " + filmId +
                    " ORDER BY USEFUL desc " +
                    "LIMIT ?";
        }
        return jdbcTemplate.query(sqlQuery, this::mapRowToReview, count);
    }

    public Review getById(long id) {
        String sqlQuery = sql + "WHERE r.REVIEW_ID = ?";
        int affected = jdbcTemplate.update("UPDATE REVIEWS set REVIEW_ID = ? where REVIEW_ID = ?", id, id);
        if (affected == 0) {
            return null;
        }
        return jdbcTemplate.queryForObject(sqlQuery, this::mapRowToReview, id);
    }

    public Review create(Review review) {
        String sqlQuery = "INSERT INTO REVIEWS (content, positive, user_id, film_id) VALUES (?, ?, ?, ?)";

        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement(sqlQuery, new String[]{"REVIEW_ID"});
            stmt.setString(1, review.getContent());
            stmt.setBoolean(2, review.getIsPositive());
            stmt.setLong(3, review.getUserId());
            stmt.setLong(4, review.getFilmId());
            return stmt;
        }, keyHolder);
        review.setReviewId(keyHolder.getKey().longValue());
        review.setUseful(0);
        log.debug("Отзыв {} записан в базу данных", review);
        return review;
    }

    public Review update(Review review) {
        String sqlQuery = "UPDATE REVIEWS SET content = ?," +
                " positive = ?" +
                "WHERE REVIEW_ID = ?";
        jdbcTemplate.update(sqlQuery,
                review.getContent(),
                review.getIsPositive(),
                review.getReviewId());
        log.debug("Отзыв {} обновлен", review);
        return getById(review.getReviewId());
    }

    public void deleteById(long id) {
        String sqlQuery = "DELETE FROM REVIEWS WHERE REVIEW_ID = ?";
        jdbcTemplate.update(sqlQuery, id);
        log.debug("Отзыв с ID {} удален", id);
    }

    public void addReactionReview(Long idReview, Long idUser, int i) {
        String sqlQuery = "MERGE INTO REVIEW_LIKE (REVIEW_ID,USER_ID,UTILITY) VALUES (?, ?, ?)";
        jdbcTemplate.update(sqlQuery, idReview, idUser, i);
        log.debug("Пользователь с ID {} поставил реакцию отзыву с ID {}", idUser, idReview);
    }

    public void deleteReactionReview(Long idReview, Long idUser) {
        String sqlQuery = "DELETE FROM REVIEW_LIKE WHERE REVIEW_ID = ? AND USER_ID = ?";
        jdbcTemplate.update(sqlQuery, idReview, idUser);
        log.debug("Пользователь с ID {} удалил реакцию к отзыву с ID {}", idUser, idReview);
    }

    private Review mapRowToReview(ResultSet rs, int id) throws SQLException {
        Review review = new Review(
                rs.getString("CONTENT"),
                rs.getBoolean("POSITIVE"),
                rs.getLong("USER_ID"),
                rs.getLong("FILM_ID")
        );
        review.setReviewId(rs.getLong("REVIEW_ID"));
        review.setUseful(rs.getInt("USEFUL"));
        return review;
    }
}
