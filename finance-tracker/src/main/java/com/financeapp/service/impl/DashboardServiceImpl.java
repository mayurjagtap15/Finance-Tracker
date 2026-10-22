package com.financeapp.service.impl;

import com.financeapp.dto.DashboardDto;
import com.financeapp.dto.TransactionDto;
import com.financeapp.model.Transaction;
import com.financeapp.model.TransactionType;
import com.financeapp.repository.TransactionRepository;
import com.financeapp.service.DashboardService;
import com.financeapp.service.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Month;
import java.time.format.TextStyle;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DashboardServiceImpl implements DashboardService {

    private final TransactionRepository transactionRepository;
    private final TransactionService transactionService;

    @Override
    public DashboardDto getDashboard(Long userId) {
        LocalDate now = LocalDate.now();
        LocalDate startOfMonth = now.withDayOfMonth(1);
        LocalDate endOfMonth = now.withDayOfMonth(now.lengthOfMonth());

        BigDecimal totalIncome = transactionRepository.sumByUserAndTypeAndDateRange(
                userId, TransactionType.INCOME, startOfMonth, endOfMonth);
        BigDecimal totalExpense = transactionRepository.sumByUserAndTypeAndDateRange(
                userId, TransactionType.EXPENSE, startOfMonth, endOfMonth);
        BigDecimal balance = totalIncome.subtract(totalExpense);

        // Recent transactions
        List<TransactionDto> recent = transactionService.findRecentByUser(userId)
                .stream().map(transactionService::toDto).collect(Collectors.toList());

        // Expense by category (current month)
        Map<String, BigDecimal> expenseByCategory = new LinkedHashMap<>();
        transactionRepository.sumByCategoryForUserAndTypeAndDateRange(
                userId, TransactionType.EXPENSE, startOfMonth, endOfMonth)
                .forEach(row -> expenseByCategory.put((String) row[0], (BigDecimal) row[1]));

        // Monthly income/expense for last 6 months
        LocalDate sixMonthsAgo = now.minusMonths(5).withDayOfMonth(1);
        List<Object[]> incomeRows  = transactionRepository.monthlyTotals(userId, TransactionType.INCOME,  sixMonthsAgo);
        List<Object[]> expenseRows = transactionRepository.monthlyTotals(userId, TransactionType.EXPENSE, sixMonthsAgo);

        List<String> monthLabels = new ArrayList<>();
        List<BigDecimal> monthlyIncome  = new ArrayList<>();
        List<BigDecimal> monthlyExpense = new ArrayList<>();

        for (int i = 5; i >= 0; i--) {
            LocalDate monthDate = now.minusMonths(i).withDayOfMonth(1);
            int m = monthDate.getMonthValue();
            int y = monthDate.getYear();
            monthLabels.add(Month.of(m).getDisplayName(TextStyle.SHORT, Locale.ENGLISH));
            monthlyIncome.add(findMonthTotal(incomeRows, m, y));
            monthlyExpense.add(findMonthTotal(expenseRows, m, y));
        }

        return new DashboardDto(totalIncome, totalExpense, balance,
                recent, expenseByCategory,
                monthLabels, monthlyIncome, monthlyExpense);
    }

    private BigDecimal findMonthTotal(List<Object[]> rows, int month, int year) {
        for (Object[] row : rows) {
            int rowMonth = ((Number) row[0]).intValue();
            int rowYear  = ((Number) row[1]).intValue();
            if (rowMonth == month && rowYear == year) {
                return (BigDecimal) row[2];
            }
        }
        return BigDecimal.ZERO;
    }
}
