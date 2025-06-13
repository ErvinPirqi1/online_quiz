package dev.ervin.online_quiz.controllers.temporary;

import dev.ervin.online_quiz.dtos.UserLoginDto;
import dev.ervin.online_quiz.dtos.UserRegistrationRequestDto;
import dev.ervin.online_quiz.models.User;
import dev.ervin.online_quiz.security.JwtUtil;
import dev.ervin.online_quiz.services.impls.UserServiceImpl;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final UserServiceImpl userService;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public AuthController(AuthenticationManager authenticationManager,
                          UserServiceImpl userService,
                          PasswordEncoder passwordEncoder,
                          JwtUtil jwtUtil) {
        this.authenticationManager = authenticationManager;
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody UserLoginDto loginRequest) {
        try {
            // Retrieve user from the database
            User user = userService.getUserByUsername(loginRequest.getUsername());
            if (user == null || !passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
                System.out.println("loginRequest.getPassword(): " + loginRequest.getPassword());
                System.out.println("Stored hashed password: " + user.getPassword());
                System.out.println("Password match result: " + passwordEncoder.matches(loginRequest.getPassword(), user.getPassword()));
                System.out.println(passwordEncoder.encode("admin123"));

                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new AuthResponse("Invalid username or password", null));
            }


            // Set up authentication token with role-based authorities
            UsernamePasswordAuthenticationToken authToken =
                    new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword(), getAuthorities(user));

            SecurityContextHolder.getContext().setAuthentication(authToken);

            // Generate JWT including user role
            String token = jwtUtil.generateToken(user.getUsername(), user.getRole());

            return ResponseEntity.ok(new AuthResponse("Login successful", token));
        } catch (AuthenticationException ex) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new AuthResponse("Invalid username or password", null));
        }
    }

    private List<GrantedAuthority> getAuthorities(User user) {
        return List.of(new SimpleGrantedAuthority("ROLE_" + user.getRole().name()));
    }




    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody UserRegistrationRequestDto dto) {
        if (userService.existsByUsername(dto.getUsername())) {
            return ResponseEntity.badRequest().body(new AuthResponse("Username is already taken", null));
        }

        if (userService.existsByEmail(dto.getEmail())) {
            return ResponseEntity.badRequest().body(new AuthResponse("Email is already taken", null));
        }

        User newUser = new User();
        newUser.setUsername(dto.getUsername());
        newUser.setPassword(passwordEncoder.encode(dto.getPassword()));
        newUser.setEmail(dto.getEmail());
        newUser.setName(dto.getName());
        newUser.setSurname(dto.getSurname());
        newUser.setRole(dto.getRole());
        newUser.setIsDeleted(false);
        newUser.setCreatedAt(LocalDateTime.now());

        userService.create(newUser);

        return ResponseEntity.ok(new AuthResponse("User registered successfully", null));
    }

    // DTO classes
    public static class LoginRequest {
        private String username;
        private String password;

        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }

        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
    }

    public static class AuthResponse {
        private String message;
        private String token;

        public AuthResponse(String message, String token) {
            this.message = message;
            this.token = token;
        }

        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }

        public String getToken() { return token; }
        public void setToken(String token) { this.token = token; }
    }
}
