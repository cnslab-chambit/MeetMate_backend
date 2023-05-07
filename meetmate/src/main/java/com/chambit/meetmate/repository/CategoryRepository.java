package com.chambit.meetmate.repository;

import com.chambit.meetmate.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Long> {
}