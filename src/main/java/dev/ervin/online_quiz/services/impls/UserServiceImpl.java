package dev.ervin.online_quiz.services.impls;

import dev.ervin.online_quiz.models.User;
import dev.ervin.online_quiz.repositories.UserRepository;
import dev.ervin.online_quiz.services.UserService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Autowired
    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public User create(User user) {
        user.setCreatedAt(LocalDateTime.now());
        user.setIsDeleted(false); // Ensure the user is not marked as deleted
        return userRepository.save(user);
    }

    @Override
    public User update(Long id, User userDetails) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User not found with ID: " + id));

        user.setName(userDetails.getName());
        user.setSurname(userDetails.getSurname());
        user.setEmail(userDetails.getEmail());
        user.setRole(userDetails.getRole());
        user.setModifiedAt(LocalDateTime.now()); // Track when the user was modified

        return userRepository.save(user);
    }

    @Override
    public User getById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User not found with ID: " + id));
    }

    @Override
    public List<User> getAll() {
        return userRepository.findAll();
    }

    @Override
    public void delete(Long id) {
        User user = getById(id);
        user.setIsDeleted(true);
        userRepository.save(user); // Save the user as deleted
    }


    @Override
    public User findByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("User not found with username: " + username));
    }
}

