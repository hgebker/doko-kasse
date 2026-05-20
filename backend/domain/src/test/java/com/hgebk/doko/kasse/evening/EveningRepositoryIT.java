package com.hgebk.doko.kasse.evening;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataMongoTest
@Testcontainers
class EveningRepositoryIT {

    @Container
    static MongoDBContainer mongo = new MongoDBContainer("mongo:7");

    @DynamicPropertySource
    static void mongoProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.mongodb.uri", mongo::getReplicaSetUrl);
    }

    @Autowired
    private EveningRepository repository;

    @AfterEach
    void tearDown() {
        repository.deleteAll();
    }

    @Test
    void givenEveningsInDifferentSemesters_whenFindBySemester_thenOnlyMatchingReturned() {
        repository.save(new Evening("2023-01-10", "ws2223", 1.0, 2.0, 3.0, 4.0, 5.0));
        repository.save(new Evening("2023-01-17", "ws2223", 6.0, 7.0, 8.0, 9.0, 10.0));
        repository.save(new Evening("2023-05-02", "ss23", 1.0, 1.0, 1.0, 1.0, 1.0));

        List<Evening> results = repository.findBySemester("ws2223");

        assertThat(results).hasSize(2)
                .allMatch(e -> "ws2223".equals(e.getSemester()));
    }

    @Test
    void givenSavedEvening_whenFindById_thenReturned() {
        Evening evening = new Evening("2024-03-15", "season2324", 2.5, 3.0, -1.5, 4.0, 0.0);
        repository.save(evening);

        assertThat(repository.findById("2024-03-15"))
                .isPresent()
                .hasValueSatisfying(e -> {
                    assertThat(e.getResultJan()).isEqualTo(2.5);
                    assertThat(e.getSemester()).isEqualTo("season2324");
                });
    }

    @Test
    void givenNoEveningsForSemester_whenFindBySemester_thenEmptyList() {
        List<Evening> results = repository.findBySemester("nonexistent");

        assertThat(results).isEmpty();
    }
}
