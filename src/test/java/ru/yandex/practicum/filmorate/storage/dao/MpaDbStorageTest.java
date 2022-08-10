package ru.yandex.practicum.filmorate.storage.dao;

import lombok.AllArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.model.films.Mpa;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
@AutoConfigureTestDatabase
@AllArgsConstructor(onConstructor_ = @Autowired)
class MpaDbStorageTest {

    private final MpaDbStorage storage;

    @Test
    void getAllMpaTest() {
        Mpa mpa = storage.getMpaById(1);
        Mpa trueMpa = new Mpa(1, "G");

        assertThat(mpa)
                .isEqualTo(trueMpa);
    }

    @Test
    void getMpaByIdTest() {
        List<Mpa> mpas = storage.getAllMpa();

        assertThat(mpas.size())
                .isEqualTo(5);
    }
}