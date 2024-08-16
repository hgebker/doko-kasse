package com.hgebk.doko.kasse.earning;

import org.socialsignin.spring.data.dynamodb.repository.EnableScan;
import org.springframework.data.repository.CrudRepository;

@EnableScan
public interface EarningRepository extends CrudRepository<Earning, String> {}
