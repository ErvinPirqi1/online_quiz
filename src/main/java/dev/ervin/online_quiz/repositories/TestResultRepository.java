package dev.ervin.online_quiz.repositories;

import dev.ervin.online_quiz.models.TestResult;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TestResultRepository extends JpaRepository<TestResult, Long> {
    List<TestResult> findAllByUserId(Long userId);
}
