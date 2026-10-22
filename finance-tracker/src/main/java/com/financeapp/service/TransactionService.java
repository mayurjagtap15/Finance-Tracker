package com.financeapp.service;

import com.financeapp.dto.TransactionDto;
import com.financeapp.model.Transaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;

public interface TransactionService {
    Transaction save(TransactionDto dto, Long userId);
    Transaction update(Long id, TransactionDto dto, Long userId);
    void delete(Long id, Long userId);
    Transaction findByIdAndUserId(Long id, Long userId);
    Page<Transaction> findAllByUser(Long userId, Pageable pageable);
    List<Transaction> findByUserAndDateRange(Long userId, LocalDate start, LocalDate end);
    List<Transaction> findRecentByUser(Long userId);
    TransactionDto toDto(Transaction transaction);
}
