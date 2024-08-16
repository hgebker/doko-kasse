package com.hgebk.doko.kasse.report;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CashReportDTO {
    private Double totalExpenses;

    private Double incomeFromEarnings;

    private Double incomeFromEvenings;

    public Double getTotalIncome() {
        return incomeFromEarnings + incomeFromEvenings;
    }

    public Double getCurrentCash() {
        return getTotalIncome() - totalExpenses;
    }
}
