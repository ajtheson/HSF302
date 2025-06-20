package com.example.demo.repository;

import com.example.demo.entity.TeacherEntity;
import com.example.demo.repository.custom.TeacherRepositoryCustom;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
public interface TeacherRepository extends JpaRepository<TeacherEntity, Integer>, TeacherRepositoryCustom {
    Optional<TeacherEntity> findByEmail(String email);
    @Modifying
    @Transactional
    @Query("UPDATE TeacherEntity t SET t.password = :password WHERE t.email = :email")
    int updatePasswordByEmail(@Param("email") String email, @Param("password") String password);
}
