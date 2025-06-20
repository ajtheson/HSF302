package com.example.demo.service;

import com.example.demo.entity.ExamEntity;

import java.util.List;

public interface ExamService {
    List<ExamEntity> findByTeacher(int teacherId);
    boolean toggleReview(int examID);
    ExamEntity searchByCode(int examCode);
    int countTeacherExams(int teacherID);
    int countOnGoingExams(int teacherID);
    boolean isValidCode(int examCode);
    List<ExamEntity> getOnGoingExams(int teacherID);
    List<ExamEntity> seachOnGoingExam(int teacherID, String nameSearch);
    List<ExamEntity> getCompletedExams(int teacherID);
    List<ExamEntity> seachCompletedExam(int teacherID, String nameSearch);
    boolean endExam(int examID) ;
    ExamEntity get(int id);
    boolean insert(ExamEntity t);
}
