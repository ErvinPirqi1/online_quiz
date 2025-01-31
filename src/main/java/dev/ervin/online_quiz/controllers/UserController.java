package dev.ervin.online_quiz.controllers;

import dev.ervin.online_quiz.dtos.UserDto;
import dev.ervin.online_quiz.dtos.UserRegistrationRequestDto;
import dev.ervin.online_quiz.services.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
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

    // Post-login handler to set user session
    @PostMapping("/login")
    public String postLogin(HttpSession session) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UserDetails userDetails = (UserDetails) auth.getPrincipal();
        UserDto userDto = userService.getUserDetails(userDetails.getUsername());
        session.setAttribute("user", userDto);
        System.out.println("User logged in: " + userDto);
        return "redirect:/"; // Redirect to the home page or any other page

    }
}
