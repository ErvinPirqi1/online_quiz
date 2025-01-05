package dev.ervin.online_quiz.repositories;

import dev.ervin.online_quiz.models.Quiz;
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

    List<Quiz> findTop9ByOrderById();



//    List<Quiz> findAllByUserId(Long userId);
//    //select * from quiz order by created_at desc limit 5
}
