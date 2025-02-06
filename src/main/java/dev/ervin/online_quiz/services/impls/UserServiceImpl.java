package dev.ervin.online_quiz.services.impls;

import dev.ervin.online_quiz.dtos.UserDto;
import dev.ervin.online_quiz.dtos.UserRegistrationRequestDto;
import dev.ervin.online_quiz.mappers.UserMapper;
import dev.ervin.online_quiz.models.User;
import dev.ervin.online_quiz.repositories.UserRepository;
import dev.ervin.online_quiz.services.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService, UserDetailsService {

    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserRepository userRepository, UserMapper userMapper, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
    }




    //  USER REGISTRATION
    @Override
    public void registerUser(UserRegistrationRequestDto userRegisterDto) {
        validateUserDetails(userRegisterDto);

        User user = userMapper.fromUserRegistrationDto(userRegisterDto);

        user.setPassword(passwordEncoder.encode(userRegisterDto.getPassword()));

        user.setCreatedAt(LocalDateTime.now());
        userRepository.save(user);
    }


    private void validateUserDetails(UserRegistrationRequestDto userRegisterDto) {
        if (userRepository.existsByUsername(userRegisterDto.getUsername())) {
            throw new IllegalArgumentException("Username already exists: " + userRegisterDto.getUsername());
        }
        if (userRepository.existsByEmail(userRegisterDto.getEmail())) {
            throw new IllegalArgumentException("Email already exists: " + userRegisterDto.getEmail());
        }
        if (!userRegisterDto.getPassword().equals(userRegisterDto.getConfirmPassword())) {
            throw new IllegalArgumentException("Passwords do not match!");
        }
    }


    //  USER MANAGEMENT (CRUD)
    @Override
    public User create(User entity) {
        return userRepository.save(entity);
    }

    @Override
    public User update(Long id, User updatedUser) {
        User existingUser = findUserById(id);
        updateUserFields(existingUser, updatedUser);
        return userRepository.save(existingUser);
    }

    private List<GrantedAuthority> getAuthorities(String role) {
        return Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + role));
    }

    private void updateUserFields(User existingUser, User updatedUser) {
        existingUser.setUsername(updatedUser.getUsername());
        existingUser.setEmail(updatedUser.getEmail());
        existingUser.setName(updatedUser.getName());
        existingUser.setSurname(updatedUser.getSurname());
        existingUser.setRole(updatedUser.getRole());
    }

    @Override
    public User getById(Long id) {
        return findUserById(id);
    }

    @Override
    public List<User> getAll() {
        return userRepository.findAll();
    }

    @Override
    public void delete(Long id) {
        userRepository.deleteById(id);
    }

    private User findUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with ID: " + id));
    }


    //  USER AUTHENTICATION (SPRING SECURITY)
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));

        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(),
                getAuthorities(user.getRole().toString())
        );
    }




    @Override
    public User getUserByUsername(String username) {
        Optional<User> userOptional = userRepository.findByUsername(username);
        return userOptional.orElse(null); // Return null if the user is not found
        // OR, if you want to throw an exception if the user is not found:
        // return userOptional.orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

    private void logUserDetails(User user) {
        logger.info("User found: {} with role: {}", user.getUsername(), user.getRole());
    }

    private UserDetails buildUserDetails(User user) {
        List<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_" + user.getRole().name()));
        return org.springframework.security.core.userdetails.User.builder()
                .username(user.getUsername())
                .password(user.getPassword())
                .authorities(authorities)
                .build();
    }


    //  GET USER DETAILS
    @Override
    public UserDto getUserDetails(String username) {
        User user = getUserByUsername(username);
        return userMapper.toDto(user);
    }

}
