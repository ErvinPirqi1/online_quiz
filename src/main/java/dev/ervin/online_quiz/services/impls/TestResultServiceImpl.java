package dev.ervin.online_quiz.services.impls;

import dev.ervin.online_quiz.models.TestResult;
import dev.ervin.online_quiz.repositories.TestResultRepository;
import dev.ervin.online_quiz.services.TestResultService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TestResultServiceImpl implements TestResultService {

    private final TestResultRepository testResultRepository;

    public TestResultServiceImpl(TestResultRepository testResultRepository) {
        this.testResultRepository = testResultRepository;
    }

    @Override
    public TestResult create(TestResult testResult) {
        return testResultRepository.save(testResult);
    }

    @Override
    public TestResult update(TestResult testResult) {
        TestResult existingTestResult = testResultRepository.findById(testResult.getId())
                .orElseThrow(() -> new EntityNotFoundException("TestResult not found"));


        existingTestResult.setScore(testResult.getScore());
        existingTestResult.setCurrentQuestionIndex(testResult.getCurrentQuestionIndex());


        return testResultRepository.save(existingTestResult); // Save the updated entity
    }

    @Override
    public TestResult update(Long id, TestResult testResultDetails) {
        TestResult testResult = testResultRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Test result not found with ID: " + id));
        testResult.setScore(testResultDetails.getScore());
        return testResultRepository.save(testResult);
    }

    @Override
    public TestResult getById(Long id) {
        return testResultRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Test result not found with ID: " + id));
    }

    @Override
    public List<TestResult> getAll() {
        return testResultRepository.findAll();
    }

    @Override
    public void delete(Long id) {
        TestResult testResult = getById(id);
        testResult.setIsDeleted(true);
        testResultRepository.save(testResult);
    }

    @Override
    public List<TestResult> getTestResultsByUser(Long userId) {
        return testResultRepository.findAllByUserId(userId);
    }

    @Override
    public TestResult save(TestResult testResult) {
        return testResultRepository.save(testResult);
    }
}
