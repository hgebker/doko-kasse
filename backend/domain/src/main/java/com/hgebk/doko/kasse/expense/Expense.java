package com.hgebk.doko.kasse.expense;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "doko-expenses")
public class Expense implements Comparable<Expense> {
    @Id
    private String id;

    private String description;

    private double value;

    private String semester;

    @Override
    public int compareTo(Expense that) {
        return this.getDescription().compareTo(that.getDescription());
    }
}
