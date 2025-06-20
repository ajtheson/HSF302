package com.example.demo.controller;

import com.example.demo.entity.TeacherEntity;
import com.example.demo.service.ExamService;
import com.example.demo.service.QuizService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class TeacherDashboardController {
    @Autowired
    private QuizService quizService;
    @Autowired
    private ExamService examService;
    @GetMapping(value = "/teacher_dashboard")
    public String teacherDashboard(Model model, HttpSession session) {
        if(session.getAttribute("account") == null) {
            return "redirect:/account/login";
        }

        TeacherEntity t = (TeacherEntity) session.getAttribute("account");
        int teacherID = t.getTeacherId();
        int countTotalQuiz = quizService.findByTeacher(teacherID).size();
        int countTotalExam = examService.findByTeacher(teacherID).size();
        model.addAttribute("numberOfQuizzes", countTotalQuiz);
        model.addAttribute("numberOfExams", countTotalExam);
        return "teacher_dashboard";
    }
}
