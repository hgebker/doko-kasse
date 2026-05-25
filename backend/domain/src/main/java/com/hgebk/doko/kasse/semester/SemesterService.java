package com.hgebk.doko.kasse.semester;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import org.springframework.data.domain.Sort;

import java.util.List;

@Service
@Slf4j
public class SemesterService {
    private final SemesterRepository semesterRepository;

    @Autowired
    public SemesterService(SemesterRepository semesterRepository) {
        this.semesterRepository = semesterRepository;
    }

    public List<Semester> getAllSemesters() {
        log.info("DBACK: Find all semesters");
        return semesterRepository.findAll(Sort.by(Sort.Direction.ASC, "sortKey"));
    }

    public Semester getSemester(String key) {
        log.info("DBACK: Find semester for key {}", key);
        return semesterRepository.findById(key).orElseThrow(() -> new SemesterNotFoundException(key));
    }

    public void saveSemester(Semester newSemester) {
        log.info("DBACK: Save new semester");
        semesterRepository.save(newSemester);
    }

    public void updateSemester(Semester updatedSemester) {
        log.info("DBACK: Find semester to update");
        if (semesterRepository.findById(updatedSemester.getKey()).isEmpty()) {
            throw new SemesterNotFoundException(updatedSemester.getKey());
        }
        semesterRepository.save(updatedSemester);
    }

    public void deleteSemesterByKey(String key) {
        log.info("DBACK: Find semester to delete");
        Semester toDelete = semesterRepository.findById(key)
                .orElseThrow(() -> new SemesterNotFoundException(key));
        semesterRepository.delete(toDelete);
    }
}
