package dev.ervin.online_quiz.controllers;

import dev.ervin.online_quiz.dtos.UserDto;
import dev.ervin.online_quiz.dtos.UserRegistrationRequestDto;
import dev.ervin.online_quiz.services.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    // POST /api/users/register
    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody UserRegistrationRequestDto userRegisterDto) {
        try {
            userService.registerUser(userRegisterDto);
            return ResponseEntity.ok("User registered successfully.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // GET /api/users/me
    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser(HttpSession session) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !(auth.getPrincipal() instanceof UserDetails)) {
            return ResponseEntity.status(401).body("Not authenticated");
        }

        UserDetails userDetails = (UserDetails) auth.getPrincipal();
        UserDto userDto = userService.getUserDetails(userDetails.getUsername());
        session.setAttribute("user", userDto); // Optional: store in session if needed
        return ResponseEntity.ok(userDto);
    }
}
