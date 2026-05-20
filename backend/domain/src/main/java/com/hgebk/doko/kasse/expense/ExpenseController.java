package com.hgebk.doko.kasse.expense;

import com.hgebk.doko.kasse.Application;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping(path = ExpenseController.PATH)
public class ExpenseController {
    public static  final String PATH = Application.BASE_PATH + "/expenses";

    private final ExpenseService expenseService;
    private final ExpenseModelAssembler expenseModelAssembler;

    @Autowired
    public ExpenseController(
            ExpenseService expenseService, ExpenseModelAssembler expenseModelAssembler
    ) {
        this.expenseService = expenseService;
        this.expenseModelAssembler = expenseModelAssembler;
    }

    @GetMapping
    public CollectionModel<EntityModel<Expense>> getAllExpenses() {
        List<EntityModel<Expense>> expenses = expenseService
                .getAllExpenses()
                .stream()
                .map(expenseModelAssembler::toModel)
                .collect(Collectors.toList());

        return CollectionModel.of(expenses, linkTo(methodOn(ExpenseController.class).getAllExpenses()).withSelfRel());
    }

    @GetMapping("/{id}")
    public EntityModel<Expense> getExpense(@PathVariable String id) {
        Expense expense = expenseService.getExpense(id);
        return expenseModelAssembler.toModel(expense);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void saveExpense(@RequestBody Expense newExpense) {
        expenseService.saveExpense(newExpense);
    }

    @PutMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateExpense(@RequestBody Expense updatedExpense) {
        expenseService.updateExpense(updatedExpense);
    }

    @DeleteMapping(path = "/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteExpenseById(@PathVariable String id) {
        expenseService.deleteExpenseById(id);
    }
}
