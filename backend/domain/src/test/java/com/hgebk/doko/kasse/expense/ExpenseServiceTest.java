package com.hgebk.doko.kasse.expense;

import com.hgebk.doko.BaseTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.AssertionsForClassTypes.catchThrowable;
import static org.mockito.BDDMockito.given;

class ExpenseServiceTest extends BaseTest {
    @Mock
    private ExpenseRepository expenseRepository;

    private ExpenseService underTest;

    @BeforeEach
    public void setUp() {
        underTest = new ExpenseService(expenseRepository);
    }

    @Test
    void getAllExpenses_withResults() {
        List<Expense> expenses = List.of(new Expense("foo1", 3.5, "foo1"), new Expense("bar1", 7, "bar1"));
        BDDMockito.given(expenseRepository.findAll()).willReturn(expenses);

        List<Expense> actualExpenses = underTest.getAllExpenses();

        Assertions.assertThat(actualExpenses).isNotNull().containsAll(expenses);
    }

    @Test
    void getAllExpenses_withoutResults() {
        List<Expense> expenses = List.of();
        BDDMockito.given(expenseRepository.findAll()).willReturn(expenses);

        List<Expense> actualExpenses = underTest.getAllExpenses();

        Assertions.assertThat(actualExpenses).isNotNull().isEmpty();
    }

    @Test
    void getExpense_found() {
        Expense expense = new Expense("foo1", 3.5, "foo1");
        BDDMockito.given(expenseRepository.findById("foo1")).willReturn(Optional.of(expense));

        Expense actualExpense = underTest.getExpense("foo1");

        Assertions.assertThat(actualExpense).isNotNull().isEqualTo(expense);
    }

    @Test
    void getExpense_notFound() {
        BDDMockito.given(expenseRepository.findById("foo1")).willReturn(Optional.empty());

        Throwable thrown = AssertionsForClassTypes.catchThrowable(() -> underTest.getExpense("foo1"));

        Assertions
                .assertThat(thrown)
                .isInstanceOf(ExpenseNotFoundException.class)
                .hasMessage("Expense with description 'foo1' does not exist");
    }

    @Test
    void saveExpense_new() {
        Expense expense = new Expense("foo1", 3.5, "foo1");
        BDDMockito.given(expenseRepository.findById("foo1")).willReturn(Optional.empty());

        Assertions.assertThatCode(() -> underTest.saveExpense(expense)).doesNotThrowAnyException();
    }

    @Test
    void saveExpense_duplicate() {
        Expense expense = new Expense("foo1", 3.5, "foo1");
        BDDMockito.given(expenseRepository.findById("foo1")).willReturn(Optional.of(expense));

        Throwable thrown = AssertionsForClassTypes.catchThrowable(() -> underTest.saveExpense(expense));

        Assertions
                .assertThat(thrown)
                .isInstanceOf(DuplicateExpenseException.class)
                .hasMessage("Expense with description foo1 already exists");
    }

    @Test
    void updateExpense_found() {
        Expense expense = new Expense("foo1", 3.5, "foo1");
        BDDMockito.given(expenseRepository.findById("foo1")).willReturn(Optional.of(expense));

        Assertions.assertThatCode(() -> underTest.updateExpense(expense)).doesNotThrowAnyException();
    }

    @Test
    void updateExpense_notFound() {
        Expense expense = new Expense("foo1", 3.5, "foo1");
        BDDMockito.given(expenseRepository.findById("foo1")).willReturn(Optional.empty());

        Throwable thrown = AssertionsForClassTypes.catchThrowable(() -> underTest.updateExpense(expense));

        Assertions
                .assertThat(thrown)
                .isInstanceOf(ExpenseNotFoundException.class)
                .hasMessage("Expense with description 'foo1' does not exist");
    }

    @Test
    void deleteExpenseByDescription_found() {
        Expense expense = new Expense("foo1", 3.5, "foo1");
        BDDMockito.given(expenseRepository.findById("foo1")).willReturn(Optional.of(expense));

        Assertions.assertThatCode(() -> underTest.deleteExpenseByDescription("foo1")).doesNotThrowAnyException();
    }

    @Test
    void deleteExpenseByDescription_notFound() {
        BDDMockito.given(expenseRepository.findById("foo1")).willReturn(Optional.empty());

        Throwable thrown = AssertionsForClassTypes.catchThrowable(() -> underTest.deleteExpenseByDescription("foo1"));

        Assertions
                .assertThat(thrown)
                .isInstanceOf(ExpenseNotFoundException.class)
                .hasMessage("Expense with description 'foo1' does not exist");
    }

    @Test
    void getTotalExpenses_twoResults() {
        List<Expense> expenses = List.of(new Expense("foo1", 3.5, "foo1"), new Expense("bar1", 7, "bar1"));
        BDDMockito.given(expenseRepository.findAll()).willReturn(expenses);

        double actualTotalExpenses = underTest.getTotalExpenses();

        Assertions.assertThat(actualTotalExpenses).isNotNull().isNotNegative().isEqualTo(10.5);
    }

    @Test
    void getTotalExpenses_noResults() {
        List<Expense> expenses = List.of();
        BDDMockito.given(expenseRepository.findAll()).willReturn(expenses);

        double actualTotalExpenses = underTest.getTotalExpenses();

        Assertions.assertThat(actualTotalExpenses).isNotNull().isZero();
    }
}