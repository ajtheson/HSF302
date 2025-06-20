package com.example.demo.repository;

import com.example.demo.entity.QuestionEntity;
import com.example.demo.repository.custom.QuestionRepositoryCustom;
import org.springframework.data.jpa.repository.JpaRepository;

public interface QuestionRepository extends JpaRepository<QuestionEntity, Integer>, QuestionRepositoryCustom {
}
