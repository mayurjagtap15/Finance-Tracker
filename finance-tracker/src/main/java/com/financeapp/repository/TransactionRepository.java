package com.financeapp.repository;

import com.financeapp.model.Transaction;
import com.financeapp.model.TransactionType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    Page<Transaction> findByUserIdOrderByTransactionDateDescCreatedAtDesc(Long userId, Pageable pageable);

    List<Transaction> findByUserIdAndTransactionDateBetweenOrderByTransactionDateDesc(
            Long userId, LocalDate start, LocalDate end);

    @Query("SELECT COALESCE(SUM(t.amount), 0) FROM Transaction t WHERE t.user.id = :userId AND t.type = :type AND t.transactionDate BETWEEN :start AND :end")
    BigDecimal sumByUserAndTypeAndDateRange(
            @Param("userId") Long userId,
            @Param("type") TransactionType type,
            @Param("start") LocalDate start,
            @Param("end") LocalDate end);

    @Query("SELECT t.category.name, COALESCE(SUM(t.amount), 0) FROM Transaction t " +
           "WHERE t.user.id = :userId AND t.type = :type AND t.transactionDate BETWEEN :start AND :end " +
           "GROUP BY t.category.name ORDER BY SUM(t.amount) DESC")
    List<Object[]> sumByCategoryForUserAndTypeAndDateRange(
            @Param("userId") Long userId,
            @Param("type") TransactionType type,
            @Param("start") LocalDate start,
            @Param("end") LocalDate end);

    @Query("SELECT FUNCTION('MONTH', t.transactionDate), FUNCTION('YEAR', t.transactionDate), COALESCE(SUM(t.amount), 0) " +
           "FROM Transaction t " +
           "WHERE t.user.id = :userId AND t.type = :type AND t.transactionDate >= :since " +
           "GROUP BY FUNCTION('YEAR', t.transactionDate), FUNCTION('MONTH', t.transactionDate) " +
           "ORDER BY FUNCTION('YEAR', t.transactionDate), FUNCTION('MONTH', t.transactionDate)")
    List<Object[]> monthlyTotals(
            @Param("userId") Long userId,
            @Param("type") TransactionType type,
            @Param("since") LocalDate since);

    List<Transaction> findTop5ByUserIdOrderByTransactionDateDescCreatedAtDesc(Long userId);
}
