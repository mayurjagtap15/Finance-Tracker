package com.financeapp.service.impl;

import com.financeapp.dto.TransactionDto;
import com.financeapp.model.Category;
import com.financeapp.model.Transaction;
import com.financeapp.model.User;
import com.financeapp.repository.CategoryRepository;
import com.financeapp.repository.TransactionRepository;
import com.financeapp.repository.UserRepository;
import com.financeapp.service.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TransactionServiceImpl implements TransactionService {

    private final TransactionRepository transactionRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;

    @Override
    @Transactional
    public Transaction save(TransactionDto dto, Long userId) {
        Transaction transaction = new Transaction();
        return populateAndSave(transaction, dto, userId);
    }

    @Override
    @Transactional
    public Transaction update(Long id, TransactionDto dto, Long userId) {
        Transaction transaction = findByIdAndUserId(id, userId);
        return populateAndSave(transaction, dto, userId);
    }

    private Transaction populateAndSave(Transaction transaction, TransactionDto dto, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        Category category = categoryRepository.findById(dto.getCategoryId())
                .orElseThrow(() -> new IllegalArgumentException("Category not found"));

        transaction.setUser(user);
        transaction.setCategory(category);
        transaction.setAmount(dto.getAmount());
        transaction.setType(dto.getType());
        transaction.setDescription(dto.getDescription());
        transaction.setTransactionDate(dto.getTransactionDate());
        return transactionRepository.save(transaction);
    }

    @Override
    @Transactional
    public void delete(Long id, Long userId) {
        Transaction transaction = findByIdAndUserId(id, userId);
        transactionRepository.delete(transaction);
    }

    @Override
    public Transaction findByIdAndUserId(Long id, Long userId) {
        Transaction transaction = transactionRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Transaction not found"));
        if (!transaction.getUser().getId().equals(userId)) {
            throw new IllegalArgumentException("Access denied");
        }
        return transaction;
    }

    @Override
    public Page<Transaction> findAllByUser(Long userId, Pageable pageable) {
        return transactionRepository.findByUserIdOrderByTransactionDateDescCreatedAtDesc(userId, pageable);
    }

    @Override
    public List<Transaction> findByUserAndDateRange(Long userId, LocalDate start, LocalDate end) {
        return transactionRepository.findByUserIdAndTransactionDateBetweenOrderByTransactionDateDesc(userId, start, end);
    }

    @Override
    public List<Transaction> findRecentByUser(Long userId) {
        return transactionRepository.findTop5ByUserIdOrderByTransactionDateDescCreatedAtDesc(userId);
    }

    @Override
    public TransactionDto toDto(Transaction transaction) {
        TransactionDto dto = new TransactionDto();
        dto.setId(transaction.getId());
        dto.setCategoryId(transaction.getCategory().getId());
        dto.setAmount(transaction.getAmount());
        dto.setType(transaction.getType());
        dto.setDescription(transaction.getDescription());
        dto.setTransactionDate(transaction.getTransactionDate());
        return dto;
    }
}
