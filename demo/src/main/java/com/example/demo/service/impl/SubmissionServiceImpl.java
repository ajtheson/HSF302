package com.example.demo.service.impl;

import com.example.demo.entity.AnswerEntity;
import com.example.demo.entity.SubmissionEntity;
import com.example.demo.repository.SubmissionRepository;
import com.example.demo.service.SubmissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class SubmissionServiceImpl implements SubmissionService {
    @Autowired
    private SubmissionRepository submissionRepository;

    @Override
    public int getExamID(int submissionID) {
        return submissionRepository.getExamID(submissionID);
    }

    @Override
    public boolean forceSubmit(int submissionID) {
        return submissionRepository.forceSubmit(submissionID);
    }

    @Override
    public boolean checkSubmit(int submissionID) {
        return submissionRepository.checkSubmit(submissionID);
    }

    @Override
    public List<SubmissionEntity> getOnGoing(int examID) {
        return submissionRepository.getOnGoing(examID);
    }

    @Override
    public int createSubmission(int studentID, int examID) {
        return submissionRepository.createSubmission(studentID, examID);
    }

    @Override
    public int finishSubmission(int submissionID, LocalDateTime submitTime, int duration, int selected, int correctAnswers, double score, List<AnswerEntity> answers) {
        return submissionRepository.finishSubmission(submissionID, submitTime, duration, selected, correctAnswers, score, answers);
    }

    @Override
    public int countAttempts(int studentID, int examID) {
        return submissionRepository.countAttempts(studentID, examID);
    }

    @Override
    public int submissionCount(int teacherID) {
        return submissionRepository.submissionCount(teacherID);
    }

    @Override
    public List<SubmissionEntity> getSubmissionOfExam(int examID) {
        return submissionRepository.getSubmissionOfExam(examID);
    }

    @Override
    public List<SubmissionEntity> getSubmissionOfStudent(int studentID) {
        return submissionRepository.getSubmissionOfStudent(studentID);
    }

    @Override
    public SubmissionEntity get(int id) {
        return submissionRepository.get(id);
    }
}
