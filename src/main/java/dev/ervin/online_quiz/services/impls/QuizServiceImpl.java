package dev.ervin.online_quiz.services.impls;

import dev.ervin.online_quiz.models.Quiz;
import dev.ervin.online_quiz.repositories.QuizRepository;
import dev.ervin.online_quiz.services.QuizService;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import org.springframework.stereotype.Service;
import java.util.Optional;

import java.util.List;

@Service
public class QuizServiceImpl implements QuizService {

    private final QuizRepository quizRepository;

    public QuizServiceImpl(QuizRepository quizRepository) {
        this.quizRepository = quizRepository;
    }

    @Override
    public Quiz create(@Valid Quiz quiz) {

        if (quizRepository.findByTitle(quiz.getTitle()).isPresent()) {
            throw new EntityExistsException("Quiz with title " + quiz.getTitle() + " already exists");
        }

        return quizRepository.save(quiz);
    }

    @Override
    public Quiz update(Long id, Quiz quizDetails) {
        Quiz quiz = quizRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Quiz not found with ID: " + id));
        quiz.setTitle(quizDetails.getTitle());
        quiz.setDescription(quizDetails.getDescription());
        quiz.setVisibility(quizDetails.getVisibility());
        quiz.setCategory(quizDetails.getCategory());
        return quizRepository.save(quiz);
    }

    @Override
    public Quiz getById(Long id) {
        return quizRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Quiz not found with ID: " + id));
    }

    @Override
    public List<Quiz> getAll() {
        return quizRepository.findAll();
    }

    @Override
    public void delete(Long id) {
        Quiz quiz = getById(id);
        quiz.setIsDeleted(true);
        quizRepository.save(quiz);
    }

//    @Override
//    public List<Quiz> getAllQuizzesByUser(Long userId) {
//        return quizRepository.findAllByUserId(userId);
//    }

    @Override
    public List<Quiz> getRecentQuizzes() {
        return quizRepository.findTop9ByOrderById();
    }

}

