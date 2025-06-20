package com.example.demo.service.impl;

import com.example.demo.entity.QuizEntity;
import com.example.demo.repository.QuizRepository;
import com.example.demo.service.QuizService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class QuizServiceImpl implements QuizService {
    @Autowired
    private QuizRepository quizRepository;

    @Override
    public List<QuizEntity> findByTeacher(int teacherId) {
        return quizRepository.findByTeacherTeacherId(teacherId);
    }

    @Override
    public int countTotalQuiz(int teacherID) {
        return quizRepository.countTotalQuiz(teacherID);
    }

    @Override
    public int countValidQuiz(int teacherID) {
        return quizRepository.countValidQuiz(teacherID);
    }

    @Override
    public int countUsedQuiz(int teacherID) {
        return quizRepository.countUsedQuiz(teacherID);
    }

    @Override
    public QuizEntity findById(int id) {
        return quizRepository.findById(id).get();
    }

    @Override
    public boolean deleteQuiz(int quizID) {
        return quizRepository.deleteQuiz(quizID);
    }

    @Override
    public boolean updateQuiz(QuizEntity q) {
        return quizRepository.updateQuiz(q);
    }

    @Override
    public boolean createQuiz(QuizEntity q) {
        return quizRepository.createQuiz(q);
    }

    @Override
    public List<QuizEntity> getTeacherQuiz(int teacherID) {
        return quizRepository.getTeacherQuiz(teacherID);
    }

    @Override
    public List<QuizEntity> searchByName(int teacherID, String name) {
        return quizRepository.searchByName(teacherID, name);
    }

    @Override
    public QuizEntity get(int id) {
        return quizRepository.findById(id).get();
    }

}
