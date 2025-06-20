package com.example.demo.controller;

import com.example.demo.entity.ChoiceEntity;
import com.example.demo.entity.QuestionEntity;
import com.example.demo.entity.QuizEntity;
import com.example.demo.entity.TeacherEntity;
import com.example.demo.service.QuizService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpSession;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.*;

@Controller
@RequestMapping("/quiz")
public class CreateQuizController {

    @Autowired
    private QuizService quizService;

    @GetMapping("/create")
    public String showCreateQuizPage() {
        return "quiz/create_quiz"; // Thymeleaf sẽ dùng create_quiz.html
    }

    @PostMapping("/create")
    public String handleCreateQuiz(
            @RequestParam("quizName") String quizName,
            @RequestParam Map<String, String> allParams,
            HttpSession session) {

        int quantity = 0;
        Map<String, QuestionEntity> questionMap = new HashMap<>();

        for (String key : allParams.keySet()) {
            if (key.startsWith("question-")) {
                quantity++;
                String questionId = key.substring(9);
                String questionContent = allParams.get(key);
                QuestionEntity q = new QuestionEntity();
                q.setContent(questionContent);
                q.setMultipleChoice(false);
                q.setDeleted(false);
                q.setChoices(new ArrayList<>());
                q.setQuiz(new QuizEntity());
                questionMap.put(questionId, q);
            }
        }

        for (String key : allParams.keySet()) {
            if (key.startsWith("choice-")) {
                String questionId = key.substring(7, 7 + 36);
                String choiceId = key.substring(7 + 36 + 1);
                String choiceContent = allParams.get(key);
                boolean isCorrect = allParams.containsKey("correct-" + questionId + "-" + choiceId);
                ChoiceEntity c = new ChoiceEntity();
                c.setChoiceContent(choiceContent);
                c.setCorrectChoice(isCorrect);
                c.setDeleted(false);
                c.setQuestion(new QuestionEntity());
                questionMap.get(questionId).getChoices().add(c);
            }
        }

        for (QuestionEntity q : questionMap.values()) {
            long correctCount = q.getChoices().stream().filter(ChoiceEntity::isCorrectChoice).count();
            q.setMultipleChoice(correctCount > 1);
        }

        List<QuestionEntity> questions = new ArrayList<>(questionMap.values());
        TeacherEntity teacher = (TeacherEntity) session.getAttribute("account");
        QuizEntity quiz = new QuizEntity();
        quiz.setQuizName(quizName);
        quiz.setQuantity(quantity);
        quiz.setDeleted(false);
        quiz.setQuestions(questions);
        quiz.setTeacher(teacher);
        try{
            quizService.createQuiz(quiz);
            return "redirect:/quiz/manager?method=view";
        }
        catch (Exception e) {
            return "error/database_error";
        }
    }
}
