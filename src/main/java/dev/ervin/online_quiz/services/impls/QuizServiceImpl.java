package dev.ervin.online_quiz.services.impls;

import dev.ervin.online_quiz.dtos.QuizDto;
import dev.ervin.online_quiz.models.Quiz;
import dev.ervin.online_quiz.repositories.QuizRepository;
import dev.ervin.online_quiz.services.QuizService;
import dev.ervin.online_quiz.mappers.QuizMapper;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class QuizServiceImpl implements QuizService {

    private final QuizRepository quizRepository;
    private final QuizMapper quizMapper;  // Assuming you have a QuizMapper to map between Quiz and QuizDto

    public QuizServiceImpl(QuizRepository quizRepository, QuizMapper quizMapper) {
        this.quizRepository = quizRepository;
        this.quizMapper = quizMapper;
    }

    @Override
    public QuizDto create(QuizDto quizDto) {
        // Convert DTO to Entity
        Quiz quizEntity = quizMapper.toEntity(quizDto);

        // Save entity
        Quiz savedQuiz = quizRepository.save(quizEntity);

        // Return the saved entity as DTO
        return quizMapper.toDto(savedQuiz);
    }

    @Override
    public QuizDto update(Long id, QuizDto quizDto) {
        Quiz existingQuiz = quizRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Quiz not found with ID: " + id));

        existingQuiz.setTitle(quizDto.getTitle());
        existingQuiz.setDescription(quizDto.getDescription());
        existingQuiz.setCategory(quizDto.getCategory());
        existingQuiz.setVisibility(quizDto.getVisibility());

        Quiz updatedQuiz = quizRepository.save(existingQuiz);
        return quizMapper.toDto(updatedQuiz);
    }

    @Override
    public QuizDto getById(Long id) {
        Quiz quiz = quizRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Quiz not found with ID: " + id));
        return quizMapper.toDto(quiz);  // Convert entity to DTO
    }

    @Override
    public List<QuizDto> getAll() {
        List<Quiz> quizzes = quizRepository.findAll();
        return quizzes.stream()
                .map(quizMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<QuizDto> getRecentQuizzes() {
        List<Quiz> recentQuizzes = quizRepository.findTop9ByOrderByCreatedAtDesc();
        return recentQuizzes.stream()
                .map(quizMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public void delete(Long id) {
        Quiz quiz = quizRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Quiz not found with ID: " + id));


        quiz.setIsDeleted(true);
        quizRepository.save(quiz);
    }


}

