package com.example.demo.repository;

import com.example.demo.entity.StudentEntity;
import com.example.demo.repository.custom.StudentRepositoryCustom;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface StudentRepository extends JpaRepository<StudentEntity, Integer>, StudentRepositoryCustom {
    Optional<StudentEntity> findByEmail(String email);
    @Modifying
    @Transactional
    @Query("UPDATE StudentEntity t SET t.password = :password WHERE t.email = :email")
    int updatePasswordByEmail(@Param("email") String email, @Param("password") String password);
}
