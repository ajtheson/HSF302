package com.example.demo.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

@Entity
public class AnswerEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer answerId;
    private String studentChoice;
    private boolean isCorrect;
    @ManyToOne
    @JoinColumn(name = "questionId")
    @JsonIgnore
    private QuestionEntity question;
    @ManyToOne
    @JoinColumn(name = "submissionId")
    @JsonIgnore
    private SubmissionEntity submission;

    public Integer getAnswerId() {
        return answerId;
    }

    public void setAnswerId(Integer answerId) {
        this.answerId = answerId;
    }

    public String getStudentChoice() {
        return studentChoice;
    }

    public void setStudentChoice(String studentChoice) {
        this.studentChoice = studentChoice;
    }

    public boolean isCorrect() {
        return isCorrect;
    }

    public void setCorrect(boolean correct) {
        isCorrect = correct;
    }

    public QuestionEntity getQuestion() {
        return question;
    }

    public void setQuestion(QuestionEntity question) {
        this.question = question;
    }

    public SubmissionEntity getSubmission() {
        return submission;
    }

    public void setSubmission(SubmissionEntity submission) {
        this.submission = submission;
    }
}
