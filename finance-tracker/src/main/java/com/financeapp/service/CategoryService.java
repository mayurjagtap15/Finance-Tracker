package com.financeapp.service;

import com.financeapp.dto.CategoryDto;
import com.financeapp.model.Category;
import com.financeapp.model.TransactionType;

import java.util.List;

public interface CategoryService {
    List<Category> findAllForUser(Long userId);
    List<Category> findAllForUserByType(Long userId, TransactionType type);
    Category save(CategoryDto dto, Long userId);
    void delete(Long id, Long userId);
    Category findById(Long id);
}
