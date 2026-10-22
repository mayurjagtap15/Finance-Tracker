package com.financeapp.repository;

import com.financeapp.model.Category;
import com.financeapp.model.TransactionType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CategoryRepository extends JpaRepository<Category, Long> {

    // Global categories (user_id IS NULL) + user-specific categories
    @Query("SELECT c FROM Category c WHERE c.user IS NULL OR c.user.id = :userId ORDER BY c.name")
    List<Category> findAllForUser(@Param("userId") Long userId);

    @Query("SELECT c FROM Category c WHERE (c.user IS NULL OR c.user.id = :userId) AND c.type = :type ORDER BY c.name")
    List<Category> findAllForUserByType(@Param("userId") Long userId, @Param("type") TransactionType type);
}
