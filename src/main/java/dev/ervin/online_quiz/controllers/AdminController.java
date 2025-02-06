package dev.ervin.online_quiz.controllers;

import dev.ervin.online_quiz.dtos.UserDto;
import dev.ervin.online_quiz.dtos.UserRegistrationRequestDto;
import dev.ervin.online_quiz.models.User;
import dev.ervin.online_quiz.services.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
//@PreAuthorize("hasAuthority('ROLE_ADMIN')")
public class AdminController {

    private final UserService userService;

    public AdminController(UserService userService) {
        this.userService = userService;
    }

    // GET /api/admin/dashboard - Placeholder dashboard endpoint
    @GetMapping("/dashboard")
    public ResponseEntity<String> dashboard() {
        return ResponseEntity.ok("Admin Dashboard Access");
    }

    // GET /api/admin/users - Get all users
    @GetMapping("/users")
    public ResponseEntity<List<User>> listUsers() {
        return ResponseEntity.ok(userService.getAll());
    }

    // POST /api/admin/users - Create new user
    @PostMapping("/users")
    public ResponseEntity<?> createUser(@RequestBody UserRegistrationRequestDto userDto) {
        try {
            userService.registerUser(userDto);
            return ResponseEntity.ok("User created successfully");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

    // DELETE /api/admin/users/{id} - Delete user by ID
    @DeleteMapping("/users/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        userService.delete(id);
        return ResponseEntity.ok("User deleted successfully");
    }
}
