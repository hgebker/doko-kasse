package com.hgebk.doko.expense;

import org.socialsignin.spring.data.dynamodb.repository.EnableScan;
import org.springframework.data.repository.CrudRepository;

@EnableScan
public interface ExpenseRepository extends CrudRepository<Expense, String> {}
