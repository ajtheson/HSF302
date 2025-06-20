package com.example.demo.controller;

import com.example.demo.entity.ChoiceEntity;
import com.example.demo.entity.QuestionEntity;
import com.example.demo.entity.QuizEntity;
import com.example.demo.entity.TeacherEntity;
import com.example.demo.repository.QuizRepository;
import com.example.demo.service.QuizService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLException;
import java.util.*;

@Controller
@RequestMapping("/quiz/manager")
public class ManagerQuizController {

    @Autowired
    private QuizService quizService;
    @Autowired
    private QuizRepository quizRepository;

    @GetMapping
    public String handleGetRequest(
            @RequestParam String method,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) Integer quizid,
            @RequestParam(required = false) String only_see,
            @RequestParam(required = false) String successQuizCreate,
            @RequestParam(required = false) String successQuizRemove,
            @RequestParam(required = false) String successQuizUpdate,
            HttpSession session,
            Model model) throws SQLException {

        TeacherEntity t = (TeacherEntity) session.getAttribute("account");

        switch (method) {
            case "view":
                return viewQuizzes(t, model, successQuizCreate, successQuizRemove, successQuizUpdate);

            case "search":
                return searchQuizzes(t, name, model);

            case "see":
                return seeQuiz(quizid, only_see, model);

            case "remove":
                return removeQuiz(quizid, model);

            case "edit":
                return editQuiz(quizid, model);

            default:
                return "error/404";
        }
    }

    @PostMapping
    public String handlePostRequest(
            @RequestParam String method,
            @RequestParam Map<String, String> allParams,
            HttpSession session,
            Model model) throws SQLException {

        TeacherEntity t = (TeacherEntity) session.getAttribute("account");

        if ("done_edit".equals(method)) {
            return doneEditQuiz(allParams, t, model);
        }

        return "error/database_error";
    }

    private String viewQuizzes(TeacherEntity t, Model model, String created, String removed, String updated) throws SQLException {
        List<QuizEntity> quizzes = quizService.getTeacherQuiz(t.getTeacherId());
        model.addAttribute("quizzes", quizzes);
        model.addAttribute("totalQuiz", quizService.countTotalQuiz(t.getTeacherId()));
        model.addAttribute("validQuiz", quizService.countValidQuiz(t.getTeacherId()));
        model.addAttribute("usedQuiz", quizService.countUsedQuiz(t.getTeacherId()));
        if (quizzes.isEmpty()) {
            model.addAttribute("quizEmpty", "No quiz available");
        }
        model.addAttribute("successQuizCreate", created);
        model.addAttribute("successQuizRemove", removed);
        model.addAttribute("successQuizUpdate", updated);
        return "quiz/quiz_list";
    }

    private String searchQuizzes(TeacherEntity t, String name, Model model) throws SQLException {
        List<QuizEntity> quizzes = quizService.searchByName(t.getTeacherId(), name);
        model.addAttribute("quizzes", quizzes);
        model.addAttribute("totalQuiz", quizService.countTotalQuiz(t.getTeacherId()));
        model.addAttribute("validQuiz", quizService.countValidQuiz(t.getTeacherId()));
        model.addAttribute("usedQuiz", quizService.countUsedQuiz(t.getTeacherId()));
        return "quiz/quiz_list";
    }

    private String seeQuiz(int quizID, String onlySee, Model model) throws SQLException {
        QuizEntity quiz = quizService.findById(quizID);
        model.addAttribute("quiz", quiz);
        model.addAttribute("only_see", onlySee);
        return "quiz/view_quiz";
    }

    private String removeQuiz(int quizID, Model model) throws SQLException {
        quizService.deleteQuiz(quizID);
        String msg = "Quiz has just been removed";
        return "redirect:/quiz/manager?method=view&successQuizRemove=" + msg;
    }

    private String editQuiz(int quizID, Model model) throws SQLException {
        QuizEntity quiz = quizService.findById(quizID);
        model.addAttribute("quiz", quiz);
        return "quiz/update_quiz";
    }

    private String doneEditQuiz(Map<String, String> params, TeacherEntity t, Model model) throws SQLException {
        int quizID = Integer.parseInt(params.get("quizid"));
        String quizName = params.get("quizName");

        Map<String, QuestionEntity> questionMap = new HashMap<>();
        int quantity = 0;

        for (String key : params.keySet()) {
            if (key.startsWith("question-")) {
                quantity++;
                String questionId = key.substring(9);
                String questionContent = params.get(key);
                QuestionEntity q = new QuestionEntity();
                q.setContent(questionContent);
                q.setMultipleChoice(false);
                q.setDeleted(false);
                q.setChoices(new ArrayList<>());
                q.setQuiz(new QuizEntity());
                questionMap.put(questionId, q);
            }
        }

        for (String key : params.keySet()) {
            String questionId;
            String choiceId;
            if (key.startsWith("choice-")) {
                if (key.length() == 80) {
                    questionId = key.substring(7, 7 + 36);
                    choiceId = key.substring(7 + 36 + 1);
                } else {
                    String[] choiceParams = key.split("-");
                    questionId = choiceParams[1];
                    choiceId = key.substring(7 + questionId.length() + 1);
                }
                String choiceContent = params.get(key);
                boolean isCorrect = params.containsKey("correct-" + questionId + "-" + choiceId);
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
        QuizEntity quiz = new QuizEntity();
        quiz.setQuizId(quizID);
        quiz.setQuizName(quizName);
        quiz.setQuantity(quantity);
        quiz.setDeleted(false);
        quiz.setQuestions(new ArrayList<>(questionMap.values()));
        quiz.setTeacher(t);
        if (quizService.updateQuiz(quiz)) {
            String msg = "Quiz '" + quiz.getQuizName() + "' has just been updated";
            return "redirect:/quiz/manager?method=view&successQuizUpdate=" + msg;
        } else {
            return "error/database_error";
        }
    }
}

