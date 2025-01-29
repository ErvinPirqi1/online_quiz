package dev.ervin.online_quiz.controllers;

import dev.ervin.online_quiz.dtos.UserLoginDto;
import dev.ervin.online_quiz.dtos.UserRegistrationRequestDto;
import dev.ervin.online_quiz.services.UserService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class UserController {

    private final UserService userService;
    private final AuthenticationManager authenticationManager;

    public UserController(UserService userService, AuthenticationManager authenticationManager) {
        this.userService = userService;
        this.authenticationManager = authenticationManager;
    }

    // Show the login page
    @GetMapping("/login")
    public String login(Model model) {
        model.addAttribute("userLoginDto", new UserLoginDto());
        return "login";
    }

    // Handle login form submission
    @PostMapping("/login")
    public String loginPost(@ModelAttribute("userLoginDto") UserLoginDto userLoginDto) {
        // Authenticate the user using AuthenticationManager
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(userLoginDto.getUsername(), userLoginDto.getPassword());

        Authentication authentication = authenticationManager.authenticate(authenticationToken);

        // Set the authentication in the SecurityContext
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // Redirect after successful login
        return "redirect:/"; // or wherever you want to redirect after successful login
    }

    // Show the registration form
    @GetMapping("/register")
    public String registerForm(Model model) {
        model.addAttribute("user", new UserRegistrationRequestDto());
        return "register";
    }

    // Handle user registration
    @PostMapping("/register")
    public String register(@ModelAttribute("user") UserRegistrationRequestDto userRegistrationDto) {
        userService.registerUser(userRegistrationDto);
        return "redirect:/login"; // Redirect to login page after registration
    }
}
