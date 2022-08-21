package ru.yandex.practicum.filmorate.storage;


import java.util.List;

public interface RecommendationsStorage {

    List<Long> getRecommendations(long id);
}
