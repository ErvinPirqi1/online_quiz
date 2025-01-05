package dev.ervin.online_quiz.repositories;

import dev.ervin.online_quiz.models.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}
