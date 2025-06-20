package com.example.demo.service.impl;

import com.example.demo.entity.QuestionEntity;
import com.example.demo.repository.QuestionRepository;
import com.example.demo.service.QuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class QuestionServiceImpl implements QuestionService {
    @Autowired
    private QuestionRepository questionRepository;

    @Override
    public QuestionEntity get(int id) {
        return questionRepository.findById(id).get();
    }
}
