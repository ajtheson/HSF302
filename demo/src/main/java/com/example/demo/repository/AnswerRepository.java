package com.example.demo.repository;

import com.example.demo.entity.AnswerEntity;
import com.example.demo.repository.custom.AnswerRepositoryCustom;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
public interface AnswerRepository extends JpaRepository<AnswerEntity, Integer>, AnswerRepositoryCustom {
    public List<AnswerEntity> findBySubmissionSubmissionId(int submissionId);
}
