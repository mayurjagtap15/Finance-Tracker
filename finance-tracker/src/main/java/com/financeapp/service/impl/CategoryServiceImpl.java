package com.financeapp.service.impl;

import com.financeapp.dto.CategoryDto;
import com.financeapp.model.Category;
import com.financeapp.model.TransactionType;
import com.financeapp.model.User;
import com.financeapp.repository.CategoryRepository;
import com.financeapp.repository.UserRepository;
import com.financeapp.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;

    @Override
    public List<Category> findAllForUser(Long userId) {
        return categoryRepository.findAllForUser(userId);
    }

    @Override
    public List<Category> findAllForUserByType(Long userId, TransactionType type) {
        return categoryRepository.findAllForUserByType(userId, type);
    }

    @Override
    @Transactional
    public Category save(CategoryDto dto, Long userId) {
        Category category;
        if (dto.getId() != null) {
            category = categoryRepository.findById(dto.getId())
                    .orElseThrow(() -> new IllegalArgumentException("Category not found"));
        } else {
            category = new Category();
        }
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        category.setName(dto.getName());
        category.setType(dto.getType());
        category.setIcon(dto.getIcon() != null ? dto.getIcon() : "bi-tag");
        category.setUser(user);
        return categoryRepository.save(category);
    }

    @Override
    @Transactional
    public void delete(Long id, Long userId) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Category not found"));
        // Only allow deletion of user-owned categories
        if (category.getUser() == null || !category.getUser().getId().equals(userId)) {
            throw new IllegalArgumentException("Cannot delete system categories");
        }
        categoryRepository.delete(category);
    }

    @Override
    public Category findById(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Category not found: " + id));
    }
}
