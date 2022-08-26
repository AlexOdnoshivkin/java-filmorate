package ru.yandex.practicum.filmorate.storage.dao;

import lombok.AllArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlGroup;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.AFTER_TEST_METHOD;
import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.BEFORE_TEST_METHOD;

@SpringBootTest
@AutoConfigureTestDatabase
@AllArgsConstructor(onConstructor_ = @Autowired)
public class RecommendationsDaoTest {

    RecommendationsDao recommendationsDao;

    @Test
    @SqlGroup({
            @Sql(value = {"get-recommendation-films.before.sql"}, executionPhase = BEFORE_TEST_METHOD),
            @Sql(value = {"get-recommendation-films.after.sql"}, executionPhase = AFTER_TEST_METHOD)
    })
    void getRecommendationTest(){
        List<Long> checkList = recommendationsDao.getRecommendations(1L);
        assertThat(checkList)
                .containsExactly(2L)
                .doesNotContain(4L);
    }

}
