package com.example.demo.service.impl;

import com.example.demo.entity.AnswerEntity;
import com.example.demo.repository.AnswerRepository;
import com.example.demo.service.AnswerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AnswerServiceImpl implements AnswerService {
    @Autowired
    private AnswerRepository answerRepository;

    @Override
    public AnswerEntity saveAnswer(AnswerEntity answer) {
        return answerRepository.save(answer);
    }

    @Override
    public List<AnswerEntity> findBySubmissionId(int submissionId) {
        return answerRepository.findBySubmissionSubmissionId(submissionId);
    }

    @Override
    public int getStudentID(int answerID) {
        return answerRepository.getStudentID(answerID);
    }

    @Override
    public List<AnswerEntity> getForStatistic(int questionID, int examID) {
        return answerRepository.getForStatistic(questionID, examID);
    }

    @Override
    public List<AnswerEntity> getAnswersOfSubmission(int submissionID) {
        return answerRepository.getAnswersOfSubmission(submissionID);
    }

    @Override
    public boolean insert(AnswerEntity t) {
        return answerRepository.insert(t);
    }
}
