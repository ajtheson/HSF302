package com.example.demo.repository;

import com.example.demo.entity.ChoiceEntity;
import com.example.demo.repository.custom.ChoiceRepositoryCustom;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChoiceRepository extends JpaRepository<ChoiceEntity, Integer>, ChoiceRepositoryCustom {
}
