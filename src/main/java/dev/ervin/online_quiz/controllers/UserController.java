package dev.ervin.online_quiz.controllers;

import dev.ervin.online_quiz.dtos.UserRegistrationRequestDto;
import dev.ervin.online_quiz.services.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    // Registration page
    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        model.addAttribute("userRegisterDto", new UserRegistrationRequestDto());
        return "register";
    }

    // Handle user registration
    @PostMapping("/register")
    public String registerUser(@ModelAttribute UserRegistrationRequestDto userRegisterDto) {
        try {
            userService.registerUser(userRegisterDto);
            return "redirect:/login?registrationSuccess"; // Redirect to login page after successful registration
        } catch (IllegalArgumentException e) {
            // Handle error (like username/email already exists)
            return "redirect:/register?error=" + e.getMessage();
        }
    }

    // Login page (Spring Security handles the login)
    @GetMapping("/login")
    public String showLoginForm() {
        return "login";
    }
}
