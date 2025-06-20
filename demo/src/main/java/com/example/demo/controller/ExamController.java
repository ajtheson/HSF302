package com.example.demo.controller;

import com.example.demo.entity.*;
import com.example.demo.service.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Controller
@RequestMapping(value = "/exam")
public class ExamController {
    @Autowired
    private SubmissionService submissionService;

    @Autowired
    private ExamService examService;

    @Autowired
    private QuizService quizService;

    @Autowired
    private StudentService studentService;

    @Autowired
    private QuestionService questionService;
    @Autowired
    private AnswerService answerService;

    @GetMapping("/check_submission")
    public Map<String, Boolean> checkSubmission(@RequestParam("submissionID") int submissionID) {
        boolean isSubmitted = submissionService.checkSubmit(submissionID);
        Map<String, Boolean> response = new HashMap<>();
        response.put("isSubmit", isSubmitted);
        return response;
    }

    @GetMapping("/completed")
    public String handleCompletedExam(
            @RequestParam(name = "method", required = false) String method,
            @RequestParam(name = "name", required = false) String name,
            HttpSession session,
            Model model) {

        TeacherEntity teacher = (TeacherEntity) session.getAttribute("account");

        if (teacher == null) {
            return "redirect:/account/login"; // hoặc xử lý khác nếu chưa đăng nhập
        }

        if ("search".equals(method)) {
            List<ExamEntity> completedExams = examService.seachCompletedExam(teacher.getTeacherId(), name);
            model.addAttribute("exams", completedExams);
        } else {
            // Mặc định là method=view hoặc null
            List<ExamEntity> completedExams = examService.getCompletedExams(teacher.getTeacherId());
            if (completedExams.isEmpty()) {
                model.addAttribute("examEmpty", "No completed exams");
            }
            model.addAttribute("exams", completedExams);
        }

        return "exam/completed_exam"; // trỏ tới file `completed_exam.html` (hoặc .jsp nếu vẫn dùng)
    }

    @GetMapping("/create")
    public String showCreateExamPage(
            @RequestParam(name = "method", required = false, defaultValue = "view") String method,
            @RequestParam(name = "name", required = false) String nameSearch,
            HttpSession session,
            Model model) {

        TeacherEntity teacher = (TeacherEntity) session.getAttribute("account");
        if (teacher == null) {
            return "redirect:/account/login"; // Xử lý chưa đăng nhập
        }

        int teacherID = teacher.getTeacherId();
        int totalQuiz = quizService.countTotalQuiz(teacherID);
        int validQuiz = quizService.countValidQuiz(teacherID);
        int usedQuiz = quizService.countUsedQuiz(teacherID);

        List<QuizEntity> quizzes;
        if ("search".equals(method) && nameSearch != null) {
            quizzes = quizService.searchByName(teacherID, nameSearch);
        } else {
            quizzes = quizService.getTeacherQuiz(teacherID);
        }

        if (quizzes.isEmpty()) {
            model.addAttribute("quizEmpty", "No quiz available");
        }

        model.addAttribute("totalQuiz", totalQuiz);
        model.addAttribute("validQuiz", validQuiz);
        model.addAttribute("usedQuiz", usedQuiz);
        model.addAttribute("quizzes", quizzes);

        return "exam/create_exam"; // Tên view (create_exam.jsp hoặc create_exam.html)
    }

    @PostMapping("/create")
    public String createExam(
            @RequestParam("quizid") int quizID,
            @RequestParam("examName") String examName,
            @RequestParam("duration") int duration,
            @RequestParam("startTime") String startTimeStr,
            @RequestParam("endTime") String endTimeStr,
            @RequestParam(name = "isReview", required = false) String isReview,
            @RequestParam("attempts") int attempts,
            Model model) {

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");
        LocalDateTime startTime = LocalDateTime.parse(startTimeStr, formatter);
        LocalDateTime endTime = LocalDateTime.parse(endTimeStr, formatter);
        boolean allowReview = isReview != null;

        // Tạo mã đề ngẫu nhiên
        Random random = new Random();
        int examCode;
        do {
            examCode = 10000 + random.nextInt(90000);
        } while (!examService.isValidCode(examCode));
        ExamEntity newExam = new ExamEntity();
        newExam.setExamId(0);
        newExam.setExamCode(examCode);
        newExam.setExamName(examName);
        newExam.setDuration(duration);
        newExam.setStartTime(startTime);
        newExam.setEndTime(endTime);
        newExam.setAttempts(attempts);
        newExam.setReview(allowReview);
        QuizEntity q = new QuizEntity();
        q.setQuizId(quizID);
        newExam.setQuiz(q);
        boolean inserted = examService.insert(newExam);
        if (inserted) {
            String successMsg = examName + " has been created. You can see it below";
            return "redirect:/exam/on_going?method=view&successExamCreate=" + successMsg;
        } else {
            return "error/database_error"; // View error
        }
    }

    @GetMapping("/result")
    public String showExamResult(
            @RequestParam("examid") int examID,
            @RequestParam(name = "method", required = false) String method,
            @RequestParam(name = "name", required = false) String nameSearch,
            HttpSession session,
            Model model) {

        TeacherEntity teacher = (TeacherEntity) session.getAttribute("account");
        if (teacher == null) {
            return "redirect:/account/login";
        }

        List<SubmissionEntity> submissions = submissionService.getSubmissionOfExam(examID);
        int totalSubmission = submissions.size();

        double sum = 0;
        double highestScore = 0;
        for (SubmissionEntity s : submissions) {
            if (s.getScore() > highestScore) {
                highestScore = s.getScore();
            }
            sum += s.getScore();
        }
        double averageScore = totalSubmission == 0 ? 0 : sum / totalSubmission;

        // Sort logic
        if ("asc".equals(method)) {
            submissions.sort(
                    Comparator.comparingDouble(SubmissionEntity::getScore)
                            .thenComparing(SubmissionEntity::getDuration, Comparator.reverseOrder())
                            .thenComparing(SubmissionEntity::getSubmitTime, Comparator.reverseOrder())
            );
        } else if ("desc".equals(method)) {
            submissions.sort(
                    Comparator.comparingDouble(SubmissionEntity::getScore).reversed()
                            .thenComparingInt(SubmissionEntity::getDuration)
                            .thenComparing(SubmissionEntity::getSubmitTime)
            );
        }

        // Search logic
        if ("search".equals(method) && nameSearch != null) {
            String searchLower = nameSearch.toLowerCase();
            submissions = submissions.stream()
                    .filter(s -> {
                        StudentEntity student = studentService.get(s.getStudent().getStudentId());
                        return student.getFullName().toLowerCase().contains(searchLower) ||
                                student.getEmail().toLowerCase().contains(searchLower);
                    })
                    .collect(Collectors.toList());
        }

        // Map: submissionID → student info
        Map<Integer, StudentEntity> studentInfoMap = new HashMap<>();
        for (SubmissionEntity s : submissions) {
            studentInfoMap.put(s.getSubmissionId(), studentService.get(s.getStudent().getStudentId()));
        }

        model.addAttribute("examName", examService.get(examID).getExamName());
        model.addAttribute("totalSubmission", totalSubmission);
        model.addAttribute("averageScore", String.format("%3.1f", averageScore));
        model.addAttribute("highestScore", String.format("%3.1f", highestScore));
        model.addAttribute("submissions", submissions);
        model.addAttribute("studentInfo", studentInfoMap);
        model.addAttribute("examid", examID);

        return "exam/exam_result";
    }

    @GetMapping("/statistic")
    public String showExamStatistic(
            @RequestParam("examID") int examID,
            @RequestParam(name = "method", required = false) String method,
            Model model) {

        List<SubmissionEntity> subList = submissionService.getSubmissionOfExam(examID);
        int totalSubmission = subList.size();

        double highestScore = 0;
        double sum = 0;
        for (SubmissionEntity s : subList) {
            if (s.getScore() > highestScore) {
                highestScore = s.getScore();
            }
            sum += s.getScore();
        }

        double averageScore = totalSubmission == 0 ? 0 : sum / totalSubmission;

        // Map<QuestionID, List<Answer>>
        Map<Integer, List<AnswerEntity>> qMap = new HashMap<>();

        for (SubmissionEntity s : subList) {
            List<AnswerEntity> answerList = answerService.getAnswersOfSubmission(s.getSubmissionId());
            for (AnswerEntity a : answerList) {
                qMap.computeIfAbsent(a.getQuestion().getQuestionId(), k -> new ArrayList<>()).add(a);
            }
        }

        // Map<QuestionID, Question>
        Map<Integer, QuestionEntity> questionMap = new HashMap<>();
        // Map<QuestionID, correct count>
        Map<Integer, Integer> correctCount = new HashMap<>();

        for (Map.Entry<Integer, List<AnswerEntity>> entry : qMap.entrySet()) {
            int questionID = entry.getKey();
            List<AnswerEntity> answers = entry.getValue();

            questionMap.put(questionID, questionService.get(questionID));

            int count = (int) answers.stream().filter(AnswerEntity::isCorrect).count();
            correctCount.put(questionID, count);
        }

        // Sắp xếp
        LinkedHashMap<Integer, Integer> sortedMap = new LinkedHashMap<>(correctCount);

        if ("asc".equals(method)) {
            sortedMap = correctCount.entrySet().stream()
                    .sorted(Map.Entry.comparingByValue())
                    .collect(Collectors.toMap(
                            Map.Entry::getKey,
                            Map.Entry::getValue,
                            (e1, e2) -> e1,
                            LinkedHashMap::new
                    ));
        } else if ("desc".equals(method)) {
            sortedMap = correctCount.entrySet().stream()
                    .sorted(Map.Entry.<Integer, Integer>comparingByValue().reversed())
                    .collect(Collectors.toMap(
                            Map.Entry::getKey,
                            Map.Entry::getValue,
                            (e1, e2) -> e1,
                            LinkedHashMap::new
                    ));
        }

        model.addAttribute("examName", examService.get(examID).getExamName());
        model.addAttribute("totalSubmission", totalSubmission);
        model.addAttribute("averageScore", String.format("%3.1f", averageScore));
        model.addAttribute("highestScore", String.format("%3.1f", highestScore));
        model.addAttribute("examID", examID);
        model.addAttribute("question", questionMap);
        model.addAttribute("correctCount", sortedMap);

        return "exam/exam_statistic"; // exam_statistic.jsp hoặc exam_statistic.html
    }

    @PostMapping("/force_submit")
    public void forceSubmit(
            @RequestParam("submissionID") int submissionID,
            HttpServletResponse response) throws IOException {

        if (submissionService.forceSubmit(submissionID)) {
            int examID = submissionService.getExamID(submissionID);

            response.setContentType("text/html");
            PrintWriter out = response.getWriter();

            out.println("<html><head><style>");
            out.println("body { display: flex; justify-content: center; align-items: center; height: 100vh; font-family: Arial, sans-serif; }");
            out.println(".loading { text-align: center; }");
            out.println(".spinner { border: 4px solid rgba(0, 0, 0, 0.1); width: 40px; height: 40px; border-radius: 50%; ");
            out.println("border-left-color: #000; animation: spin 1s linear infinite; }");
            out.println("@keyframes spin { from { transform: rotate(0deg); } to { transform: rotate(360deg); } }");
            out.println("</style>");

            out.println("<script>");
            out.println("setTimeout(function() { window.location.href = '/exam/on_going_detail?method=view&examid=" + examID + "'; }, 2000);");
            out.println("</script></head><body>");

            out.println("<div class='loading'>");
            out.println("<div class='spinner'></div>");
            out.println("<p>Processing, please wait...</p>");
            out.println("</div>");

            out.println("</body></html>");

        } else {
            response.sendRedirect("error/database_error");
        }
    }

    @GetMapping("/join")
    public String showJoinPage(@RequestParam(value = "method", required = false) String method,
                               @RequestParam(value = "codeNotFound", required = false) String codeNotFound,
                               @RequestParam(value = "examCode", required = false) String code,
                               HttpSession session,
                               Model model) {
        if ("search".equals(method)) {
            model.addAttribute("codeNotFound", codeNotFound);
            return "exam/join_exam"; // Tên của view JSP hoặc Thymeleaf (join_exam.jsp/html)
        }
        if("join".equals(method)) {
            try {
                int examCode = Integer.parseInt(code);
                StudentEntity student = (StudentEntity) session.getAttribute("account");

                ExamEntity exam = examService.searchByCode(examCode);
                if (exam == null) {
                    return "redirect:/exam/join?method=search&codeNotFound=Exam not found!";
                }

                int attempts = submissionService.countAttempts(student.getStudentId(), exam.getExamId());
                if (attempts >= exam.getAttempts()) {
                    return "redirect:/exam/join?method=search&codeNotFound=No have more attempts";
                }

                // Lấy đề gốc và bản shuffle
                QuizEntity originalQuiz = quizService.get(exam.getQuiz().getQuizId());
                QuizEntity randomQuiz = quizService.get(exam.getQuiz().getQuizId());

                Collections.shuffle(randomQuiz.getQuestions());
                for (QuestionEntity q : randomQuiz.getQuestions()) {
                    Collections.shuffle(q.getChoices());
                }

                session.setAttribute("exam", exam);
                session.setAttribute("quizReal", originalQuiz);
                session.setAttribute("quiz", randomQuiz);
                model.addAttribute("exam", session.getAttribute("exam"));
                model.addAttribute("quiz", session.getAttribute("quiz"));
                return "redirect:/exam/take";
            } catch (NumberFormatException e) {
                return "redirect:/exam/join?method=search&codeNotFound=Exam not found!";
            }
        }
        return "redirect:/exam/join?method=search";
    }


    @GetMapping("/on_going_detail")
    public String viewOngoingExamDetail(@RequestParam("examid") int examID,
                                        @RequestParam(value = "method", required = false) String method,
                                        HttpSession session,
                                        Model model) {

        TeacherEntity teacher = (TeacherEntity) session.getAttribute("account");
        if (teacher == null) {
            return "redirect:/account/login"; // hoặc trang lỗi nếu chưa đăng nhập
        }

        // Lấy danh sách bài làm của kỳ thi
        List<SubmissionEntity> submissions = submissionService.getOnGoing(examID);

        if ("submitted".equals(method)) {
            submissions = submissions.stream()
                    .filter(SubmissionEntity::isSubmit)
                    .collect(Collectors.toList());
        } else if ("unsubmitted".equals(method)) {
            submissions = submissions.stream()
                    .filter(s -> !s.isSubmit())
                    .collect(Collectors.toList());
        }

        // Lấy thông tin sinh viên cho từng submission
        Map<Integer, StudentEntity> studentInfo = new HashMap<>();
        for (SubmissionEntity s : submissions) {
            studentInfo.put(s.getSubmissionId(), studentService.get(s.getStudent().getStudentId()));
        }

        model.addAttribute("submissions", submissions);
        model.addAttribute("studentInfo", studentInfo);
        model.addAttribute("examid", examID);
        model.addAttribute("examName", examService.get(examID).getExamName());

        return "exam/on_going_detail";
    }

    @GetMapping("/on_going")
    public String handleOnGoing(@RequestParam String method,
                                @RequestParam(required = false) String name,
                                @RequestParam(required = false) Integer examid,
                                @RequestParam(required = false) String successExamCreate,
                                @RequestParam(required = false) String successExamEnd,
                                HttpSession session,
                                Model model) {

        TeacherEntity teacher = (TeacherEntity) session.getAttribute("account");
        if (teacher == null) {
            return "redirect:/login";
        }

        int teacherID = teacher.getTeacherId();

        switch (method) {
            case "view" -> {
                List<ExamEntity> onGoingExams = examService.getOnGoingExams(teacherID);
                if (onGoingExams.isEmpty()) {
                    model.addAttribute("examEmpty", "No on-going exams");
                }
                model.addAttribute("exams", onGoingExams);
                model.addAttribute("successExamCreate", successExamCreate);
                model.addAttribute("successExamEnd", successExamEnd);
                return "exam/on_going_exam"; // JSP hoặc Thymeleaf
            }

            case "search" -> {
                List<ExamEntity> results = examService.seachOnGoingExam(teacherID, name);
                model.addAttribute("exams", results);
                return "exam/on_going_exam";
            }

            case "end" -> {
                if (examid == null) return "redirect:/error";
                boolean ended = examService.endExam(examid);
                if (ended) {
                    String msg = "Exam " + examService.get(examid).getExamName() + " has been ended";
                    return "redirect:/exam/on_going?method=view&successExamEnd=" + msg.replace(" ", "%20");
                } else {
                    return "redirect:/error/database_error";
                }
            }

            default -> {
                return "redirect:/error";
            }
        }
    }

    @GetMapping("/question_statistic")
    public String showQuestionStatistic(@RequestParam("questionID") int questionID,
                                        @RequestParam("examID") int examID,
                                        @RequestParam(value = "search", required = false) String search,
                                        Model model) {

        QuestionEntity question = questionService.get(questionID);
        List<AnswerEntity> answers = answerService.getForStatistic(questionID, examID);

        Map<Integer, List<Integer>> choiceS = new HashMap<>();
        Map<Integer, StudentEntity> students = new HashMap<>();
        Map<Integer, String> choiceC = new HashMap<>();
        Map<Integer, Boolean> isCorrect = new HashMap<>();

        // Lấy nội dung các đáp án
        for (ChoiceEntity c : question.getChoices()) {
            choiceC.put(c.getChoiceId(), c.getChoiceContent());
        }

        int selected = 0, correct = 0;

        // Lặp qua từng câu trả lời
        for (AnswerEntity a : answers) {
            int studentID = answerService.getStudentID(a.getAnswerId());
            StudentEntity student = studentService.get(studentID);
            students.put(studentID, student);
            isCorrect.put(studentID, a.isCorrect());

            if (a.isCorrect()) correct++;

            List<Integer> selectedChoices = new ArrayList<>();
            String c = a.getStudentChoice();
            if (c != null && !c.trim().isEmpty()) {
                for (String x : c.trim().split(" ")) {
                    selectedChoices.add(Integer.parseInt(x));
                }
                selected++;
            }

            choiceS.put(studentID, selectedChoices);
        }

        // Lọc theo tên hoặc email nếu có
        if (search != null && !search.trim().isEmpty()) {
            choiceS = filterChoices(choiceS, search.trim());
        }

        int incorrect = selected - correct;

        model.addAttribute("questionID", questionID);
        model.addAttribute("examID", examID);
        model.addAttribute("selected", selected);
        model.addAttribute("correct", correct);
        model.addAttribute("incorrect", incorrect);
        model.addAttribute("choiceList", choiceS);
        model.addAttribute("corrects", isCorrect);
        model.addAttribute("student", students);
        model.addAttribute("choiceC", choiceC);
        model.addAttribute("question", question);

        return "exam/question_statistic"; // tên file JSP hoặc Thymeleaf
    }

    private Map<Integer, List<Integer>> filterChoices(Map<Integer, List<Integer>> choiceS, String search) {
        Map<Integer, List<Integer>> filteredMap = new HashMap<>();
        for (Map.Entry<Integer, List<Integer>> entry : choiceS.entrySet()) {
            int studentID = entry.getKey();
            StudentEntity student = studentService.get(studentID);
            if (student != null &&
                    (student.getEmail().contains(search) || student.getFullName().contains(search))) {
                filteredMap.put(studentID, entry.getValue());
            }
        }
        return filteredMap;
    }

    @GetMapping("/take")
    public String showExam(HttpSession session, Model model) {
        ExamEntity exam = (ExamEntity) session.getAttribute("exam");
        QuizEntity quiz = (QuizEntity) session.getAttribute("quiz");
        StudentEntity student = (StudentEntity) session.getAttribute("account");

        if (exam == null || quiz == null || student == null) {
            return "redirect:/exam/join?method=search";
        }

        int submissionID = submissionService.createSubmission(student.getStudentId(), exam.getExamId());
        session.setAttribute("submissionID", submissionID);
        model.addAttribute("exam", exam);
        model.addAttribute("quiz", quiz);

        return "exam/take_exam"; // JSP hoặc Thymeleaf template
    }

    @PostMapping("/take")
    public String submitExam(@RequestParam Map<String, String> paramMap,
                             @RequestParam("duration") int duration,
                             HttpSession session,
                             Model model) {

        LocalDateTime submitTime = LocalDateTime.now();
        StudentEntity student = (StudentEntity) session.getAttribute("account");
        QuizEntity quiz = (QuizEntity) session.getAttribute("quizReal");
        ExamEntity exam = (ExamEntity) session.getAttribute("exam");
        Integer submissionID = (Integer) session.getAttribute("submissionID");

        // Tách các lựa chọn của học sinh
        Map<String, String[]> rawMap = new HashMap<>();
        for (Map.Entry<String, String> entry : paramMap.entrySet()) {
            if (!entry.getKey().equals("duration")) {
                rawMap.put(entry.getKey(), entry.getValue().trim().split(" "));
            }
        }

        List<Map.Entry<String, String[]>> studentChoice = new ArrayList<>(rawMap.entrySet());
        studentChoice.sort(Comparator.comparingInt(e -> Integer.parseInt(e.getKey())));
        for (Map.Entry<String, String[]> entry : studentChoice) {
            Arrays.sort(entry.getValue(), Comparator.comparingInt(Integer::parseInt));
        }

        int selected = studentChoice.size();
        int correctAnswer = 0;
        Map<String, String> answerKey = new HashMap<>();

        // Tạo đáp án đúng
        for (QuestionEntity q : quiz.getQuestions()) {
            StringBuilder correctChoice = new StringBuilder();
            for (ChoiceEntity c : q.getChoices()) {
                if (c.isCorrectChoice()) {
                    correctChoice.append(c.getChoiceId()).append(" ");
                }
            }
            answerKey.put(String.valueOf(q.getQuestionId()), correctChoice.toString());
        }

        List<AnswerEntity> answerList = new ArrayList<>();
        int index = 0;
        for (String qID : answerKey.keySet()) {
            if (index >= selected) {
                AnswerEntity answer = new AnswerEntity();
                answer.setAnswerId(0);
                answer.setStudentChoice("");
                answer.setCorrect(false);
                QuestionEntity q = new QuestionEntity();
                q.setQuestionId(Integer.parseInt(qID));
                answer.setQuestion(q);
                SubmissionEntity submission = new SubmissionEntity();
                submission.setSubmissionId(0);
                answer.setSubmission(submission);
                answerList.add(answer);
                continue;
            }
            boolean isCorrect;
            String choiceStr = "";
            if (qID.equals(studentChoice.get(index).getKey())) {
                for (String c : studentChoice.get(index).getValue()) {
                    choiceStr += c + " ";
                }
                isCorrect = choiceStr.equals(answerKey.get(qID));
                if (isCorrect) correctAnswer++;
                AnswerEntity answer = new AnswerEntity();
                answer.setAnswerId(0);
                answer.setStudentChoice(choiceStr);
                answer.setCorrect(isCorrect);
                QuestionEntity q = new QuestionEntity();
                q.setQuestionId(Integer.parseInt(qID));
                answer.setQuestion(q);
                SubmissionEntity submission = new SubmissionEntity();
                submission.setSubmissionId(0);
                answer.setSubmission(submission);
                answerList.add(answer);
                index++;
            } else {
                AnswerEntity answer = new AnswerEntity();
                answer.setAnswerId(0);
                answer.setStudentChoice("");
                answer.setCorrect(false);
                QuestionEntity q = new QuestionEntity();
                q.setQuestionId(Integer.parseInt(qID));
                answer.setQuestion(q);
                SubmissionEntity submission = new SubmissionEntity();
                submission.setSubmissionId(0);
                answer.setSubmission(submission);
                answerList.add(answer);
            }
        }

        double score = ((double) correctAnswer / quiz.getQuantity()) * 10.0;

        int sID = submissionService.finishSubmission(
                submissionID, submitTime, duration, selected, correctAnswer, score, answerList
        );

        if (sID != -1) {
            session.removeAttribute("exam");
            session.removeAttribute("quiz");
            session.removeAttribute("quizReal");
            session.removeAttribute("submissionID");
            return "redirect:/submission/result?submissionid=" + submissionID;
        } else {
            return "error/database_error";
        }
    }

    @GetMapping("/toggle_review")
    public String toggleReview(@RequestParam("examid") int examId,
                               @RequestParam("from") String from,
                               HttpServletRequest request) {

        boolean result = examService.toggleReview(examId);

        if (result) {
            if ("com".equals(from)) {
                return "redirect:/exam/completed?method=view";
            } else {
                return "redirect:/exam/on_going?method=view";
            }
        } else {
            request.setAttribute("error", "Toggle review failed due to database error.");
            return "error/database_error";
        }
    }

    // Nếu bạn muốn hỗ trợ cả POST (như servlet ban đầu)
    @PostMapping("/toggle_review")
    public String toggleReviewPost(@RequestParam("examid") int examId,
                                   @RequestParam("from") String from,
                                   HttpServletRequest request) {
        return toggleReview(examId, from, request);
    }
}
