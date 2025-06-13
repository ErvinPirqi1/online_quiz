package dev.ervin.online_quiz.repositories;

import dev.ervin.online_quiz.models.Quiz;
import dev.ervin.online_quiz.models.User;
import jakarta.validation.constraints.NotBlank;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;
import java.util.List;

@Repository
public interface QuizRepository extends JpaRepository<Quiz, Long> {

    long countAllByTitle(@NotBlank(message = "Title is required") String title);
    //select count(*) from quiz where title = ?

    Optional<Quiz> findByTitle(String title);
    //select * from quiz where title = ?


    List<Quiz> findTop9ByOrderByCreatedAtDesc();
//select * from quiz order by created_at desc limit 9

    List<Quiz> findByParticipantsUsername(String username);

    List<Quiz> findByCreatedByUsername(String createdByUsername);

    @Override
    Optional<Quiz> findById(Long id); // Add logging here

    List<Quiz> findByCreatedBy(User user);

    List<Quiz> findByParticipantsContaining(User user);

    List<Quiz> findByCreatedByNotAndParticipantsNotContaining(User user, User user1);

//    Optional<Quiz> findByIdWithLog(Long id);
}