package dev.ervin.online_quiz.controllers;

import dev.ervin.online_quiz.services.UserService;
import org.springframework.stereotype.Controller;

@Controller
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }



}
