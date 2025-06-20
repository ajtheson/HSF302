package com.example.demo.repository.custom;

import com.example.demo.entity.AnswerEntity;

import java.util.List;

public interface AnswerRepositoryCustom {
    int getStudentID(int answerID);
    List<AnswerEntity> getForStatistic(int questionID, int examID);
    List<AnswerEntity> getAnswersOfSubmission(int submissionID);
    boolean insert(AnswerEntity t);
}
