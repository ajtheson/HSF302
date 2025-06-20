package com.example.demo.service.impl;

import com.example.demo.repository.ChoiceRepository;
import com.example.demo.service.ChoiceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ChoiceServiceImpl implements ChoiceService {
    @Autowired
    private ChoiceRepository choiceRepository;
}
