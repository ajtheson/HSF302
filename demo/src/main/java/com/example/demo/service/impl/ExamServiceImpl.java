package com.example.demo.service.impl;

import com.example.demo.entity.ExamEntity;
import com.example.demo.repository.ExamRepository;
import com.example.demo.service.ExamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ExamServiceImpl implements ExamService {
    @Autowired
    private ExamRepository examRepository;

    @Override
    public List<ExamEntity> findByTeacher(int teacherId) {
        return examRepository.findByQuizTeacherTeacherId(teacherId);
    }

    @Override
    public boolean toggleReview(int examID) {
        return examRepository.toggleReview(examID);
    }

    @Override
    public ExamEntity searchByCode(int examCode) {
        return examRepository.searchByCode(examCode);
    }

    @Override
    public int countTeacherExams(int teacherID) {
        return examRepository.countTeacherExams(teacherID);
    }

    @Override
    public int countOnGoingExams(int teacherID) {
        return examRepository.countOnGoingExams(teacherID);
    }

    @Override
    public boolean isValidCode(int examCode) {
        return examRepository.isValidCode(examCode);
    }

    @Override
    public List<ExamEntity> getOnGoingExams(int teacherID) {
        return examRepository.getOnGoingExams(teacherID);
    }

    @Override
    public List<ExamEntity> seachOnGoingExam(int teacherID, String nameSearch) {
        return examRepository.seachOnGoingExam(teacherID, nameSearch);
    }

    @Override
    public List<ExamEntity> getCompletedExams(int teacherID) {
        return examRepository.getCompletedExams(teacherID);
    }

    @Override
    public List<ExamEntity> seachCompletedExam(int teacherID, String nameSearch) {
        return examRepository.seachCompletedExam(teacherID, nameSearch);
    }

    @Override
    public boolean endExam(int examID) {
        return examRepository.endExam(examID);
    }

    @Override
    public ExamEntity get(int id) {
        return examRepository.get(id);
    }

    @Override
    public boolean insert(ExamEntity t) {
        return examRepository.insert(t);
    }
}
