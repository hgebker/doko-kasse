package com.hgebk.doko.kasse.expense;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.NoSuchElementException;

@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class ExpenseNotFoundException extends NoSuchElementException {
    public ExpenseNotFoundException(String id) {
        super(String.format("Expense with id '%s' does not exist", id));
    }
}
