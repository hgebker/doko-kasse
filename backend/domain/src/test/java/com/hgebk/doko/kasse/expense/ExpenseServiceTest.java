package com.hgebk.doko.kasse.expense;

import com.hgebk.doko.kasse.BaseTest;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.given;

class ExpenseServiceTest extends BaseTest {
    @Mock
    private ExpenseRepository expenseRepository;

    @InjectMocks
    private ExpenseService underTest;

    @Test
    void getAllExpenses_withResults() {
        List<Expense> expenses = List.of(new Expense("id-1", "foo1", 3.5, "ws2425"), new Expense("id-2", "bar1", 7, "ws2425"));
        given(expenseRepository.findAll()).willReturn(expenses);

        List<Expense> actualExpenses = underTest.getAllExpenses();

        assertThat(actualExpenses).isNotNull().containsAll(expenses);
    }

    @Test
    void getAllExpenses_withoutResults() {
        List<Expense> expenses = List.of();
        given(expenseRepository.findAll()).willReturn(expenses);

        List<Expense> actualExpenses = underTest.getAllExpenses();

        assertThat(actualExpenses).isNotNull().isEmpty();
    }

    @Test
    void getExpense_found() {
        Expense expense = new Expense("id-1", "foo1", 3.5, "ws2425");
        given(expenseRepository.findById("id-1")).willReturn(Optional.of(expense));

        Expense actualExpense = underTest.getExpense("id-1");

        assertThat(actualExpense).isNotNull().isEqualTo(expense);
    }

    @Test
    void getExpense_notFound() {
        given(expenseRepository.findById("id-1")).willReturn(Optional.empty());

        Throwable thrown = catchThrowable(() -> underTest.getExpense("id-1"));

        assertThat(thrown)
                .isInstanceOf(ExpenseNotFoundException.class)
                .hasMessage("Expense with id 'id-1' does not exist");
    }

    @Test
    void saveExpense_savesWithoutDuplicateCheck() {
        Expense expense = new Expense(null, "foo1", 3.5, "ws2425");

        assertThatCode(() -> underTest.saveExpense(expense)).doesNotThrowAnyException();
    }

    @Test
    void updateExpense_found() {
        Expense expense = new Expense("id-1", "foo1", 3.5, "ws2425");
        given(expenseRepository.findById("id-1")).willReturn(Optional.of(expense));

        assertThatCode(() -> underTest.updateExpense(expense)).doesNotThrowAnyException();
    }

    @Test
    void updateExpense_notFound() {
        Expense expense = new Expense("id-1", "foo1", 3.5, "ws2425");
        given(expenseRepository.findById("id-1")).willReturn(Optional.empty());

        Throwable thrown = catchThrowable(() -> underTest.updateExpense(expense));

        assertThat(thrown)
                .isInstanceOf(ExpenseNotFoundException.class)
                .hasMessage("Expense with id 'id-1' does not exist");
    }

    @Test
    void deleteExpenseById_found() {
        Expense expense = new Expense("id-1", "foo1", 3.5, "ws2425");
        given(expenseRepository.findById("id-1")).willReturn(Optional.of(expense));

        assertThatCode(() -> underTest.deleteExpenseById("id-1")).doesNotThrowAnyException();
    }

    @Test
    void deleteExpenseById_notFound() {
        given(expenseRepository.findById("id-1")).willReturn(Optional.empty());

        Throwable thrown = catchThrowable(() -> underTest.deleteExpenseById("id-1"));

        assertThat(thrown)
                .isInstanceOf(ExpenseNotFoundException.class)
                .hasMessage("Expense with id 'id-1' does not exist");
    }

    @Test
    void getTotalExpenses_twoResults() {
        List<Expense> expenses = List.of(new Expense("id-1", "foo1", 3.5, "ws2425"), new Expense("id-2", "bar1", 7, "ws2425"));
        given(expenseRepository.findAll()).willReturn(expenses);

        double actualTotalExpenses = underTest.getTotalExpenses();

        assertThat(actualTotalExpenses).isNotNull().isNotNegative().isEqualTo(10.5);
    }

    @Test
    void getTotalExpenses_noResults() {
        List<Expense> expenses = List.of();
        given(expenseRepository.findAll()).willReturn(expenses);

        double actualTotalExpenses = underTest.getTotalExpenses();

        assertThat(actualTotalExpenses).isNotNull().isZero();
    }
}
