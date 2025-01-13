package dev.ervin.online_quiz.controllers;

import dev.ervin.online_quiz.dtos.UserRegistrationRequestDto;
import dev.ervin.online_quiz.models.User;
import dev.ervin.online_quiz.services.UserService;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import java.time.LocalDateTime;

@Controller
public class AuthController {

        private final UserService userService;

        public AuthController(UserService userService) {
            this.userService = userService;
        }

        @GetMapping("/auth/register")
        public String showRegistrationForm(Model model) {
            model.addAttribute("user", new UserRegistrationRequestDto());
            return "register";  // Return registration page (Thymeleaf template)
        }

        @PostMapping("/auth/register")
        public String registerUser(@Validated UserRegistrationRequestDto registrationRequestDto,
                                   BindingResult result, Model model) {
            if (result.hasErrors()) {
                return "register"; // Return to registration page if validation fails
            }

            // Create new User entity without encoding the password
            User user = new User();
            user.setUsername(registrationRequestDto.getUsername());
            user.setPassword(registrationRequestDto.getPassword()); // No password encoding here
            user.setName(registrationRequestDto.getName());
            user.setSurname(registrationRequestDto.getSurname());
            user.setEmail(registrationRequestDto.getEmail());
            user.setRole(registrationRequestDto.getRole());
            user.setCreatedAt(LocalDateTime.now());

            userService.create(user);  // Save the user to the database

            model.addAttribute("message", "Registration successful!");
            return "login";  // Redirect to login page after successful registration
        }
}
