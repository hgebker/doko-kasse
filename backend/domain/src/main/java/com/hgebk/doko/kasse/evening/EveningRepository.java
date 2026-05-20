package com.hgebk.doko.kasse.evening;

import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface EveningRepository extends MongoRepository<Evening, String> {
    List<Evening> findBySemester(String semester);
}
