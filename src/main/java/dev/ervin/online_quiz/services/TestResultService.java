package dev.ervin.online_quiz.services;

import dev.ervin.online_quiz.models.TestResult;

import java.util.List;

public interface TestResultService extends BaseService<TestResult, Long> {
    TestResult update(TestResult testResult);

    List<TestResult> getTestResultsByUser(Long userId);

    TestResult save(TestResult testResult);
}

