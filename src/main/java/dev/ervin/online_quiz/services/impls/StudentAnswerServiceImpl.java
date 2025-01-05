package dev.ervin.online_quiz.services.impls;

import dev.ervin.online_quiz.models.StudentAnswer;
import dev.ervin.online_quiz.repositories.StudentAnswerRepository;
import dev.ervin.online_quiz.services.StudentAnswerService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StudentAnswerServiceImpl implements StudentAnswerService {

    private final StudentAnswerRepository studentAnswerRepository;

    public StudentAnswerServiceImpl(StudentAnswerRepository studentAnswerRepository) {
        this.studentAnswerRepository = studentAnswerRepository;
    }

    @Override
    public StudentAnswer create(StudentAnswer studentAnswer) {
        return studentAnswerRepository.save(studentAnswer);
    }

    @Override
    public StudentAnswer update(Long id, StudentAnswer studentAnswerDetails) {
        StudentAnswer studentAnswer = studentAnswerRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Student answer not found with ID: " + id));
        studentAnswer.setSelectedOption(studentAnswerDetails.getSelectedOption());
        studentAnswer.setIsCorrect(studentAnswerDetails.getIsCorrect());
        return studentAnswerRepository.save(studentAnswer);
    }

    @Override
    public StudentAnswer getById(Long id) {
        return studentAnswerRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Student answer not found with ID: " + id));
    }

    @Override
    public List<StudentAnswer> getAll() {
        return studentAnswerRepository.findAll();
    }

    @Override
    public void delete(Long id) {
        StudentAnswer studentAnswer = getById(id);
        studentAnswer.setIsDeleted(true);
        studentAnswerRepository.save(studentAnswer);
    }

    @Override
    public List<StudentAnswer> getAnswersByTestResult(Long testResultId) {
        return studentAnswerRepository.findAllByTestResultId(testResultId);
    }
}
