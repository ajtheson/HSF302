package com.example.demo.controller;

import com.example.demo.entity.StudentEntity;
import com.example.demo.entity.TeacherEntity;
import com.example.demo.service.StudentService;
import com.example.demo.service.TeacherService;
import com.example.demo.util.PasswordEncoder;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Controller
@RequestMapping(value = "/setting")
public class SettingController {
    @Autowired
    private StudentService studentService;
    @Autowired
    private TeacherService teacherService;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping(value = "/student")
    public String student() {
        return "setting/student_setting";
    }

    @PostMapping(value = "/student")
    public String handlePost(
            @RequestParam("mode") String mode,
            @RequestParam Map<String, String> params,
            @SessionAttribute("account") StudentEntity student,
            Model model,
            HttpSession session
    ) {
        if ("password".equals(mode)) {
            return handleChangePassword(params, student, session, model);
        }

        if ("profile".equals(mode)) {
            return handleUpdateProfile(params, student, session, model);
        }

        return "redirect:/setting/student_setting"; // fallback
    }

    private String handleChangePassword(Map<String, String> params, StudentEntity student, HttpSession session, Model model) {
        String oldPassword = params.get("old_password");
        String newPassword = params.get("new_password");
        String rePassword = params.get("re_password");

        if (!passwordEncoder.encode(oldPassword).equals(student.getPassword())) {
            model.addAttribute("passwordError", "Old password is not incorrect!");
            return "setting/student_setting";
        }

        if (!newPassword.matches("^[a-zA-Z0-9]{8,}$")) {
            model.addAttribute("passwordError", "Password contains at least 8 characters from alphanumeric characters");
            return "setting/student_setting";
        }

        if (!newPassword.equals(rePassword)) {
            model.addAttribute("passwordError", "Confirm password is not the same as password!");
            return "setting/student_setting";
        }

        if (!studentService.changePassword(student.getEmail(), passwordEncoder.encode(newPassword))) {
            model.addAttribute("changeError", "Error occurred when changing your password. Please try again");
            return "setting/student_setting";
        }

        student.setPassword(newPassword);
        session.setAttribute("account", student);
        model.addAttribute("passwordSuccess", "Your password has been changed");
        return "setting/student_setting";
    }

    private String handleUpdateProfile(Map<String, String> params, StudentEntity student, HttpSession session, Model model) {
        String fullname = params.get("fullname");
        String className = params.get("className");
        String school = params.get("school");

        String[] updateStudent = new String[]{fullname, className, school};

        if (!studentService.update(student, updateStudent)) {
            model.addAttribute("profileError", "Error occurred when changing your profile. Please try later");
            return "student/student_setting";
        }

        student.setFullName(fullname);
        student.setClassName(className);
        student.setSchool(school);
        session.setAttribute("account", student);
        model.addAttribute("profileSuccess", "Profile has been updated");
        return "student/student_setting";
    }

    @GetMapping(value = "/teacher")
    public String teacher() {
        return "setting/teacher_setting";
    }

    @PostMapping(value = "/teacher")
    public String handlePost(
            @RequestParam("mode") String mode,
            @RequestParam(required = false) String old_password,
            @RequestParam(required = false) String new_password,
            @RequestParam(required = false) String re_password,
            @RequestParam(required = false) String fullname,
            @RequestParam(required = false) String school,
            HttpSession session,
            Model model) {

        TeacherEntity t = (TeacherEntity) session.getAttribute("account");

        if ("password".equals(mode)) {
            String passwordError = "";

            if (passwordEncoder.encode(old_password).equals(t.getPassword())) {
                String regex = "^[a-zA-Z0-9]{8,}$";

                if (new_password.matches(regex)) {
                    if (new_password.equals(re_password)) {
                        if (!teacherService.changePassword(t.getEmail(), passwordEncoder.encode(new_password))) {
                            model.addAttribute("changeError", "Error occurred when changing your password. Please try again");
                            return "setting/teacher_setting";
                        }
                        t.setPassword(new_password);
                        session.setAttribute("account", t);
                        model.addAttribute("passwordSuccess", "Your password has been changed");
                        return "setting/teacher_setting";
                    } else {
                        passwordError = "Confirm password is not the same as password!";
                    }
                } else {
                    passwordError = "Password must be at least 8 alphanumeric characters";
                }
            } else {
                passwordError = "Old password is not correct!";
            }

            model.addAttribute("passwordError", passwordError);
            return "setting/teacher_setting";
        }

        if ("profile".equals(mode)) {
            String[] updateTeacher = new String[]{fullname, school};

            if (!teacherService.update(t, updateTeacher)) {
                model.addAttribute("profileError", "Error occurred when updating your profile. Please try later");
                return "setting/teacher_setting";
            }

            t.setFullName(fullname);
            t.setSchool(school);
            session.setAttribute("account", t);
            model.addAttribute("profileSuccess", "Profile has been updated");
            return "setting/teacher_setting";
        }

        return "redirect:/setting/teacher_setting";
    }
}
