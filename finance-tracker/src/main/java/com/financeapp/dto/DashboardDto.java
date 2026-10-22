package com.financeapp.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
public class DashboardDto {
    private BigDecimal totalIncome;
    private BigDecimal totalExpense;
    private BigDecimal balance;
    private List<TransactionDto> recentTransactions;
    private Map<String, BigDecimal> expenseByCategory;
    private List<String> monthLabels;
    private List<BigDecimal> monthlyIncome;
    private List<BigDecimal> monthlyExpense;
}
