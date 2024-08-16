package com.hgebk.doko.semester;

import org.socialsignin.spring.data.dynamodb.repository.EnableScan;
import org.springframework.data.repository.CrudRepository;

@EnableScan
public interface SemesterRepository extends CrudRepository<Semester, String> {}
