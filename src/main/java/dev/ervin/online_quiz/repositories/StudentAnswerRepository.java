package dev.ervin.online_quiz.repositories;

import dev.ervin.online_quiz.models.StudentAnswer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StudentAnswerRepository extends JpaRepository<StudentAnswer, Long> {
    List<StudentAnswer> findAllByTestResultId(Long testResultId);
}
