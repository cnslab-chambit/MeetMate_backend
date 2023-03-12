package com.chambit.meetmate.repository;

import com.chambit.meetmate.entity.SearchDocument;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<SearchDocument, Long> {
}