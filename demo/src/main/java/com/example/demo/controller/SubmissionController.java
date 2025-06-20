package com.example.demo.controller;

import com.example.demo.entity.*;
import com.example.demo.service.*;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequestMapping(value = "/submission")
public class SubmissionController {
    @Autowired
    private SubmissionService submissionService;
    @Autowired
    private AnswerService answerService;
    @Autowired
    private QuestionService questionService;
    @Autowired
    private StudentService studentService;
    @Autowired
    private ExamService examService;

    @GetMapping("/detail")
    public String showSubmissionDetail(@RequestParam("submissionid") int submissionID,
                                       HttpSession session,
                                       Model model) {

        SubmissionEntity submission = submissionService.get(submissionID);
        List<AnswerEntity> answers = answerService.getAnswersOfSubmission(submissionID);

        Map<Integer, QuestionEntity> questions = new HashMap<>();
        for (AnswerEntity a : answers) {
            questions.put(a.getQuestion().getQuestionId(), questionService.get(a.getQuestion().getQuestionId()));
        }

        // Role check
        Object account = session.getAttribute("account");
        String role = (account instanceof TeacherEntity) ? "teacher" : "student";

        // Student info
        String studentName = studentService.get(submission.getStudent().getStudentId()).getFullName();

        // Set model attributes
        model.addAttribute("role", role);
        model.addAttribute("studentName", studentName);
        model.addAttribute("submission", submission);
        model.addAttribute("answers", answers);
        model.addAttribute("questions", questions);

        return "submission/submission_detail"; // Thymeleaf template name (submission_detail.html)
    }

    @GetMapping("/list")
    public String showSubmissionList(@RequestParam(value = "method", required = false) String method,
                                     @RequestParam(value = "name", required = false) String nameSearch,
                                     HttpSession session,
                                     Model model) {

        StudentEntity student = (StudentEntity) session.getAttribute("account");

        if (student == null) {
            return "redirect:/account/login";
        }

        List<SubmissionEntity> submissions = submissionService.getSubmissionOfStudent(student.getStudentId());

        // Thống kê
        int totalSubmission = submissions.size();
        double sum = submissions.stream().mapToDouble(SubmissionEntity::getScore).sum();
        double averageScore = totalSubmission == 0 ? 0 : sum / totalSubmission;
        double highestScore = submissions.stream()
                .mapToDouble(SubmissionEntity::getScore)
                .max()
                .orElse(0);

        // Sắp xếp hoặc lọc theo method
        if ("newest".equals(method)) {
            submissions.sort(Comparator.comparing(SubmissionEntity::getSubmitTime).reversed());
        } else if ("oldest".equals(method)) {
            submissions.sort(Comparator.comparing(SubmissionEntity::getSubmitTime));
        } else if ("search".equals(method) && nameSearch != null) {
            String searchLower = nameSearch.toLowerCase();
            submissions = submissions.stream()
                    .filter(a -> examService.get(a.getExam().getExamId())
                            .getExamName().toLowerCase().contains(searchLower))
                    .collect(Collectors.toList());
        }

        // Lấy tên kỳ thi theo submissionID
        Map<Integer, String> examNameMap = new HashMap<>();
        for (SubmissionEntity s : submissions) {
            examNameMap.put(s.getSubmissionId(), examService.get(s.getExam().getExamId()).getExamName());
        }

        // Gửi dữ liệu lên Thymeleaf
        model.addAttribute("totalSubmission", totalSubmission);
        model.addAttribute("averageScore", String.format("%.1f", averageScore));
        model.addAttribute("highestScore", String.format("%.1f", highestScore));
        model.addAttribute("submissions", submissions);
        model.addAttribute("examName", examNameMap);

        return "submission/submission_list"; // Trỏ tới submission_list.html
    }

    @GetMapping("/result")
    public String viewSubmissionResult(@RequestParam("submissionid") int submissionID,
                                       @RequestParam(value = "method", required = false) String method,
                                       Model model, HttpSession session) {
        SubmissionEntity submission = submissionService.get(submissionID);
        ExamEntity exam = examService.get(submission.getExam().getExamId());

        if (method != null) {
            // Nếu giáo viên đã tắt chế độ review
            if (!exam.isReview()) {
                model.addAttribute("notReview", "The teacher has turned off the review feature");
                model.addAttribute("examName", exam.getExamName());
                model.addAttribute("submission", submission);
                return "submission/submission_result"; // Thymeleaf sẽ render submission_result.html
            } else {
                // Nếu được phép review, redirect sang chi tiết bài làm
                return "redirect:/submission/detail?submissionid=" + submissionID;
            }
        } else {
            // Trường hợp không có method => luôn hiển thị submission_result.jsp
            model.addAttribute("examName", exam.getExamName());
            model.addAttribute("submission", submission);
            return "submission/submission_result";
        }
    }
}
