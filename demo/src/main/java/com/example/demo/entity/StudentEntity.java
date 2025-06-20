package com.example.demo.entity;

import jakarta.persistence.*;

import java.util.List;

@Entity
public class StudentEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer studentId;
    private String email;
    private String password;
    private String fullName;
    private String className;
    private String school;
    @OneToMany(mappedBy = "student")
    private List<SubmissionEntity> submissions;

    public Integer getStudentId() {
        return studentId;
    }

    public void setStudentId(Integer studentId) {
        this.studentId = studentId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getSchool() {
        return school;
    }

    public void setSchool(String school) {
        this.school = school;
    }

    public List<SubmissionEntity> getSubmissions() {
        return submissions;
    }

    public void setSubmissions(List<SubmissionEntity> submissions) {
        this.submissions = submissions;
    }
}
