package com.hgebk.doko.kasse.expense;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class ExpenseService {
    private final ExpenseRepository expenseRepository;

    @Autowired
    public ExpenseService(ExpenseRepository expenseRepository) {
        this.expenseRepository = expenseRepository;
    }

    public List<Expense> getAllExpenses() {
        log.info("DBACK: Find all expenses");
        List<Expense> expenses = (List<Expense>) expenseRepository.findAll();
        return expenses.stream().sorted().toList();
    }

    public Expense getExpense(String id) {
        log.info("DBACK: Find expense with id {}", id);
        return expenseRepository.findById(id).orElseThrow(() -> new ExpenseNotFoundException(id));
    }

    public void saveExpense(Expense newExpense) {
        log.info("DBACK: Save new expense");
        expenseRepository.save(newExpense);
    }

    public void updateExpense(Expense updatedExpense) {
        log.info("DBACK: Find expense to update");
        if (expenseRepository.findById(updatedExpense.getId()).isEmpty()) {
            throw new ExpenseNotFoundException(updatedExpense.getId());
        }

        expenseRepository.save(updatedExpense);
    }

    public void deleteExpenseById(String id) {
        log.info("DBACK: Find expense to delete");
        Expense toDelete = expenseRepository
                .findById(id)
                .orElseThrow(() -> new ExpenseNotFoundException(id));

        expenseRepository.delete(toDelete);
    }

    public Double getTotalExpenses() {
        log.info("DBACK: Get total from expenses");
        List<Expense> allExpenses = (List<Expense>) expenseRepository.findAll();
        return allExpenses.stream().mapToDouble(Expense::getValue).sum();
    }
}
