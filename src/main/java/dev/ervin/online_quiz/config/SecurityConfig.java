package dev.ervin.online_quiz.config;

import dev.ervin.online_quiz.security.JwtAuthenticationFilter;
import dev.ervin.online_quiz.security.JwtUtil;
import dev.ervin.online_quiz.services.impls.UserServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    private final UserServiceImpl userServiceImpl;
    private final JwtUtil jwtUtil;

    public SecurityConfig(@Lazy UserServiceImpl userServiceImpl, JwtUtil jwtUtil) {
        this.userServiceImpl = userServiceImpl;
        this.jwtUtil = jwtUtil;
    }

    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter() {
        return new JwtAuthenticationFilter(jwtUtil, userServiceImpl);
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors(AbstractHttpConfigurer::disable)  // Enable CORS support properly
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        // Allow OPTIONS requests without authentication (CORS preflight)
                        .requestMatchers(HttpMethod.OPTIONS, "/api/**").permitAll()

                        // Public endpoints
                        .requestMatchers("/api/auth/**").permitAll()
                        .requestMatchers("/api/users/me").permitAll()

                        // Homepage and quizzes accessible to all
                        .requestMatchers("/", "/api/quizzes", "/api/quizzes/**").permitAll()

                        // Only TEACHER & ADMIN can create quizzes
                        .requestMatchers("/api/quizzes").hasAnyRole("TEACHER", "ADMIN")

                        // All other requests need authentication
                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}
