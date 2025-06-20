package com.example.demo.repository;

import com.example.demo.entity.SubmissionEntity;
import com.example.demo.repository.custom.SubmissionRepositoryCustom;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SubmissionRepository extends JpaRepository<SubmissionEntity, Integer>, SubmissionRepositoryCustom {
}
