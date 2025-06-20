package com.example.demo.controller;

import com.example.demo.dto.StudentDTO;
import com.example.demo.dto.TeacherDTO;
import com.example.demo.entity.StudentEntity;
import com.example.demo.entity.TeacherEntity;
import com.example.demo.service.StudentService;
import com.example.demo.service.TeacherService;
import com.example.demo.util.EmailSender;
import com.example.demo.util.PasswordEncoder;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Random;

@Controller
@RequestMapping(value = "/account")
public class AccountController {
    @Autowired
    private StudentService studentService;
    @Autowired
    private TeacherService teacherService;
    @Autowired
    private EmailSender emailSender;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @GetMapping(value = "/login")
    public String loginPage(HttpSession session, Model model) {
        if(session.getAttribute("errFirstLogin") != null) {
            model.addAttribute("errFirstLogin", session.getAttribute("errFirstLogin"));
            session.removeAttribute("errFirstLogin");
        }
        return "account/login";
    }
    @PostMapping(value = "/login")
    public String login(@RequestParam("email") String email, @RequestParam("password") String password, @RequestParam("role") String role,Model model, HttpSession session) {
        String error = "";
        if(role.equals("teacher")){
            if(teacherService.findByEmail(email).isEmpty()){
                error = "Email does not exist!";
            }else{
                String encodePassword = passwordEncoder.encode(password);
                TeacherEntity t = teacherService.findByEmail(email).get();
                if(encodePassword.equals(t.getPassword())){
                    session.setAttribute("account", t);
                    return "redirect:/teacher_dashboard";
                }else {
                    error = "Wrong password!";
                }
            }
            model.addAttribute("email", email);
            model.addAttribute("password", password);
            model.addAttribute("role", role);
            model.addAttribute("loginError", error);
            return "account/login";
        }else {
            if(studentService.findByEmail(email).isEmpty()){
                error = "Email does not exist!";
            }else {
                String encodePassword = passwordEncoder.encode(password);
                StudentEntity s = studentService.findByEmail(email).get();
                if(encodePassword.equals(s.getPassword())){
                    session.setAttribute("account", s);
                    return "redirect:/exam/join?method=search";
                }else {
                    error = "Wrong password!";
                }
            }
            model.addAttribute("email", email);
            model.addAttribute("password", password);
            model.addAttribute("role", role);
            model.addAttribute("loginError", error);
            return "account/login";
        }
    }

    @GetMapping(value="/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/account/login";
    }
    @GetMapping(value = "/register")
    public String registerPage() {
        return "account/register";
    }

    @GetMapping(value = "/register_student")
    public String registerStudent(Model model) {
        model.addAttribute("student", new StudentDTO());
        return "account/register_student";
    }

    @PostMapping("/register_student")
    public String registerStudent(@ModelAttribute StudentDTO form,
                                  HttpSession session,
                                  Model model) {
        String error = "";

        if (studentService.findByEmail(form.getEmail()).isPresent()) {
            error = "Email has been existed!";
        } else {
            String regex = "^[a-zA-Z0-9]{8,}$";
            if (form.getPassword().matches(regex)) {
                if (!form.getPassword().equals(form.getRepassword())) {
                    error = "Confirm password is not the same as password!";
                } else {
                    int otp = 1000 + new Random().nextInt(9000);
                    String message = "Your activating OTP number is " + otp + ". Note that this OTP is active only 5 minutes!";
                    try {
                        emailSender.sendEmail(form.getEmail(), "Activating Account", message);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    StudentEntity tempStudent = new StudentEntity();
                    tempStudent.setEmail(form.getEmail());
                    tempStudent.setPassword(passwordEncoder.encode(form.getPassword()));
                    tempStudent.setFullName(form.getFullname());
                    tempStudent.setClassName(form.getClassName());
                    tempStudent.setSchool(form.getSchool());
                    session.setAttribute("tempStudent", tempStudent);
                    session.setAttribute("otp_check", otp);
                    session.setAttribute("email", form.getEmail());
                    session.setAttribute("mode", "student_register");
                    session.setMaxInactiveInterval(300);

                    model.addAttribute("activateAlert", "An OTP code has been sent to your email. Type to activate your account. Note that this OTP is active for 5 minutes only!");
                    return "account/activate";
                }
            } else {
                error = "Password must contain at least 8 alphanumeric characters.";
            }
        }

        // Gửi lại dữ liệu đã nhập và lỗi
        model.addAttribute("student", form);
        model.addAttribute("registerError", error);
        return "account/register_student";
    }
    @GetMapping(value = "/register_teacher")
    public String registerTeacher(Model model) {
        model.addAttribute("teacher", new TeacherDTO());
        return "account/register_teacher";
    }

    @PostMapping("/register_teacher")
    public String registerTeacher(@ModelAttribute TeacherDTO form,
                                  HttpSession session,
                                  Model model) {
        String error = "";

        if (teacherService.findByEmail(form.getEmail()).isPresent()) {
            error = "Email has been existed!";
        } else {
            String regex = "^[a-zA-Z0-9]{8,}$"; // at least 8 alphanumeric chars
            if (form.getPassword().matches(regex)) {
                if (!form.getPassword().equals(form.getRepassword())) {
                    error = "Confirm password is not the same as password!";
                } else {
                    int otp = 1000 + new Random().nextInt(9000);
                    String message = "Your activating OTP number is " + otp + ". Note that this OTP is only active for 5 minutes!";
                    try {
                        emailSender.sendEmail(form.getEmail(), "Activating Account", message);
                    } catch (Exception e) {
                        e.printStackTrace(); // log properly in real app
                    }

                    TeacherEntity tempTeacher = new TeacherEntity();
                    tempTeacher.setEmail(form.getEmail());
                    tempTeacher.setPassword(passwordEncoder.encode(form.getPassword()));
                    tempTeacher.setFullName(form.getFullname());
                    tempTeacher.setSchool(form.getSchool());
                    session.setMaxInactiveInterval(300); // 5 minutes
                    session.setAttribute("tempTeacher", tempTeacher);
                    session.setAttribute("otp_check", otp);
                    session.setAttribute("email", form.getEmail());
                    session.setAttribute("mode", "teacher_register");

                    model.addAttribute("activateAlert", "An OTP code has been sent to your email. Type it to activate your account. Note: OTP is valid for 5 minutes only!");
                    return "account/activate";
                }
            } else {
                error = "Password must contain at least 8 alphanumeric characters.";
            }
        }

        // Gửi lại dữ liệu và lỗi nếu có
        model.addAttribute("teacher", form);
        model.addAttribute("registerError", error);
        return "account/register_teacher";
    }

    @GetMapping(value = "/forgot")
    public String forgotPage() {
        return "account/forgot";
    }

    @PostMapping("/forgot")
    public String handleForgotPassword(
            @RequestParam String email,
            @RequestParam String role,
            HttpSession session,
            Model model) {

        boolean found = false;
        if ("teacher".equals(role) && teacherService.findByEmail(email).isPresent()) {
            found = true;
            session.setAttribute("mode", "teacher_forgot");
        } else if ("student".equals(role) && studentService.findByEmail(email).isPresent()) {
            found = true;
            session.setAttribute("mode", "student_forgot");
        }
        if (found) {
            int otp = 1000 + new Random().nextInt(9000);
            String message = "Your OTP number is " + otp + ". Note that this OTP is active in only 5 minutes!";
            try {
                emailSender.sendEmail(email, "Change Account Password", message);
            } catch (Exception e) {
                e.printStackTrace();
            }

            session.setMaxInactiveInterval(300);
            session.setAttribute("otp_check", otp);
            session.setAttribute("email", email);
            model.addAttribute("activateAlert", "An OTP code has been sent to your email. Please type it to continue. This code is valid for 5 minutes only.");
            return "account/activate";
        }

        model.addAttribute("email", email);
        model.addAttribute("role", role);
        model.addAttribute("forgotError", "Email does not exist!");
        return "account/forgot";
    }

    @PostMapping("/activate")
    public String verifyOTP(@RequestParam("otp") String otpInput,
                            HttpSession session,
                            Model model) {

        String email = (String) session.getAttribute("email");
        Integer otpCheck = (Integer) session.getAttribute("otp_check");
        String mode = (String) session.getAttribute("mode");

        if (otpCheck != null && otpInput.equals(otpCheck.toString())) {
            switch (mode) {
                case "student_register":
                    StudentEntity tempStudent = (StudentEntity) session.getAttribute("tempStudent");
                    if (studentService.register(tempStudent) != null) {
                        session.removeAttribute("otp_check");
                        session.removeAttribute("email");
                        session.removeAttribute("mode");
                        model.addAttribute("successRegister", "Your account has been successfully activated. Please login");
                    } else {
                        model.addAttribute("registerError", "Error occurred when registering. Please try later.");
                        session.invalidate();
                    }
                    return "account/login";

                case "teacher_register":
                    TeacherEntity tempTeacher = (TeacherEntity) session.getAttribute("tempTeacher");
                    if (teacherService.register(tempTeacher) != null) {
                        session.removeAttribute("otp_check");
                        session.removeAttribute("email");
                        session.removeAttribute("mode");
                        model.addAttribute("successRegister", "Your account has been successfully activated. Please login");
                    } else {
                        model.addAttribute("registerError", "Error occurred when registering. Please try later.");
                        session.invalidate();
                    }
                    return "account/login";

                case "student_forgot":
                    session.removeAttribute("otp_check");
                    session.removeAttribute("mode");
                    model.addAttribute("changeRole", "student");
                    model.addAttribute("successOTP", "Verified successfully. Please change password");
                    return "account/forgot";

                case "teacher_forgot":
                    session.removeAttribute("otp_check");
                    session.removeAttribute("mode");
                    model.addAttribute("changeRole", "teacher");
                    model.addAttribute("successOTP", "Verified successfully. Please change password");
                    return "account/forgot";

                default:
                    model.addAttribute("failOTP", "Invalid mode");
                    return "account/activate";
            }
        } else {
            model.addAttribute("failOTP", "OTP number is not correct");
            return "account/activate";
        }
    }

    @PostMapping("/change_password")
    public String changePassword(@RequestParam("password") String password,
                                 @RequestParam("repassword") String repassword,
                                 @RequestParam("role") String role,
                                 HttpSession session,
                                 Model model) {

        String email = (String) session.getAttribute("email");
        String changeError = "";
        String regex = "^[a-zA-Z0-9]{8,}$";

        if (password.matches(regex)) {
            if (!password.equals(repassword)) {
                changeError = "Confirm password is not the same as password!";
            } else {
                int success = 0;

                if ("student".equals(role)) {
                    success = studentService.updatePasswordByEmail(email, passwordEncoder.encode(password));
                } else if ("teacher".equals(role)) {
                    success = teacherService.updatePasswordByEmail(email, passwordEncoder.encode(password));
                }

                if (success == 0) {
                    session.invalidate();
                    model.addAttribute("changePasswordError", "Error occurred when changing your password. Please try later.");
                    return "account/login";
                }

                session.removeAttribute("email");
                model.addAttribute("successChange", "Your password has been changed successfully, please login!");
                return "account/login";
            }
        } else {
            changeError = "Password must contain at least 8 alphanumeric characters.";
        }

        // Set lại thông tin khi có lỗi để hiển thị ở forgot.jsp
        model.addAttribute("password", password);
        model.addAttribute("repassword", repassword);
        model.addAttribute("changeRole", role);
        model.addAttribute("changeError", changeError);

        return "account/forgot";
    }

    @GetMapping("/resend_otp")
    public String resendOtp(HttpSession session, Model model) {
        session.setMaxInactiveInterval(300); // OTP hết hạn sau 5 phút

        Random random = new Random();
        int otp = 1000 + random.nextInt(9000);

        String email = (String) session.getAttribute("email");
        if (email == null) {
            model.addAttribute("activateAlert", "Email not found in session. Please register again.");
            return "account/activate";
        }

        String message = "Your OTP number is " + otp + ". Note that this OTP is active in only 5 minutes!";

        try {
            emailSender.sendEmail(email, "Resend OTP Number", message);
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("activateAlert", "Failed to send OTP. Please try again later.");
            return "account/activate";
        }

        session.setAttribute("otp_check", otp);
        model.addAttribute("activateAlert", "An OTP code has been resent to your email");

        return "account/activate";
    }
}
