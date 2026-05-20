package com.hgebk.doko.kasse.report;

public record CashReportDTO(Double totalExpenses, Double incomeFromEarnings, Double incomeFromEvenings) {

    public Double getTotalIncome() {
        return incomeFromEarnings + incomeFromEvenings;
    }

    public Double getCurrentCash() {
        return getTotalIncome() - totalExpenses;
    }
}
