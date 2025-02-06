package dev.ervin.online_quiz.services;

import dev.ervin.online_quiz.models.StudentAnswer;

import java.util.List;

public interface StudentAnswerService extends BaseService<StudentAnswer, Long> {
    List<StudentAnswer> getAnswersByTestResult(Long testResultId);


    StudentAnswer save(StudentAnswer studentAnswer);
}

