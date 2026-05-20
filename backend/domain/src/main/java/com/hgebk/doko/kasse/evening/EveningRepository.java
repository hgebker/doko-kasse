package com.hgebk.doko.kasse.evening;

import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface EveningRepository extends CrudRepository<Evening, String> {
    List<Evening> findBySemester(String semester);
}
