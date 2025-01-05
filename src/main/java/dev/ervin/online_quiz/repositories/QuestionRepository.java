package dev.ervin.online_quiz.repositories;

import dev.ervin.online_quiz.models.Question;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface QuestionRepository extends JpaRepository<Question, Long> {
    List<Question> findByQuizId(Long quizId);

    List<Question> findAllByQuizId(Long quizId);
}
