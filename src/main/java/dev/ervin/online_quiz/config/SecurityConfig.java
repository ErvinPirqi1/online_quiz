package dev.ervin.online_quiz.config;

import dev.ervin.online_quiz.config.CustomAuthenticationFailureHandler;
import dev.ervin.online_quiz.services.impls.UserServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final UserServiceImpl userServiceImpl;
    private final CustomAuthenticationFailureHandler failureHandler;

    public SecurityConfig(@Lazy UserServiceImpl userServiceImpl, CustomAuthenticationFailureHandler failureHandler) {
        this.userServiceImpl = userServiceImpl;
        this.failureHandler = failureHandler;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        String[] publicUrls = {"/", "/login", "/register", "/quiz/list"};  // Add /quiz/list to the public URLs
        String[] staticResources = {"/static/**", "/assets/**", "/css/**", "/js/**", "/images/**", "/fonts/**"};

        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(authz -> authz
                        .requestMatchers(staticResources).permitAll()  // Allow access to static resources
                        .requestMatchers(publicUrls).permitAll()  // Allow access to the public URLs
                        .requestMatchers("/teacher/**").hasRole("TEACHER")  // Require role TEACHER for /teacher/** URLs
                        .requestMatchers("/student/**").hasRole("STUDENT")  // Require role STUDENT for /student/** URLs
                        .anyRequest().authenticated()  // Any other request requires authentication
                )
                .formLogin(form -> form
                        .loginPage("/login")
                        .loginProcessingUrl("/login")  // Ensure this matches your form action
                        .failureHandler(failureHandler)
                        .defaultSuccessUrl("/", true)
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/")
                        .invalidateHttpSession(true)
                        .deleteCookies("JSESSIONID")
                        .permitAll()
                )
                .sessionManagement(session -> session
                        .maximumSessions(1)
                        .expiredUrl("/login?expired")
                );

        return http.build();
    }





    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }


    public void testPasswordVerification(String rawPassword, String storedPassword) {
        boolean matches = passwordEncoder().matches(rawPassword, storedPassword);
        System.out.println("Password matches: " + matches);
    }


    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }
}
