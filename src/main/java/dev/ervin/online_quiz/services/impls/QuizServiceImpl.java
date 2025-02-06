package dev.ervin.online_quiz.services.impls;

import dev.ervin.online_quiz.dtos.QuizDto;
import dev.ervin.online_quiz.models.Question;
import dev.ervin.online_quiz.models.Quiz;
import dev.ervin.online_quiz.models.User;
import dev.ervin.online_quiz.repositories.QuestionRepository;
import dev.ervin.online_quiz.repositories.QuizRepository;
import dev.ervin.online_quiz.repositories.UserRepository;
import dev.ervin.online_quiz.services.QuizService;
import dev.ervin.online_quiz.mappers.QuizMapper;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class QuizServiceImpl implements QuizService {

    private final UserRepository userRepository;
    private final QuizRepository quizRepository;
    private final QuizMapper quizMapper;
    private final QuestionRepository questionRepository;

    @Autowired
    public QuizServiceImpl(UserRepository userRepository, QuizRepository quizRepository, QuizMapper quizMapper, QuestionRepository questionRepository) {
        this.userRepository = userRepository;
        this.quizRepository = quizRepository;
        this.quizMapper = quizMapper;
        this.questionRepository = questionRepository;
    }

    @Override
    public Quiz create(Quiz quiz) { // Take the Quiz entity directly
        return quizRepository.save(quiz); // No need for manual mapping here
    }

    @Override
    public QuizDto update(Long id, QuizDto quizDto) {
        Quiz existingQuiz = quizRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Quiz not found"));

        existingQuiz.setTitle(quizDto.getTitle());
        existingQuiz.setDescription(quizDto.getDescription());
        existingQuiz.setCategory(quizDto.getCategory());
        existingQuiz.setVisibility(quizDto.getVisibility());
        existingQuiz.setStatus(quizDto.getStatus());
        existingQuiz.setModifiedAt(LocalDateTime.now());

        User modifiedBy = userRepository.findByUsername(quizDto.getModifiedByUsername())
                .orElseThrow(() -> new EntityNotFoundException("User not found: " + quizDto.getModifiedByUsername()));
        existingQuiz.setModifiedBy(modifiedBy);

        Quiz updatedQuiz = quizRepository.save(existingQuiz);
        return quizMapper.toDto(updatedQuiz);
    }

    @Override
    public void toggleVisibility(Long id, QuizDto quizDto) {
        Quiz quiz = quizRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Quiz not found"));

        quiz.setVisibility((short) (quiz.getVisibility() == 0 ? 1 : 0)); // Toggle 0 and 1
        quiz.setModifiedAt(LocalDateTime.now());
        User modifiedBy = userRepository.findByUsername(quizDto.getModifiedByUsername())
                .orElseThrow(() -> new EntityNotFoundException("User not found: " + quizDto.getModifiedByUsername()));
        quiz.setModifiedBy(modifiedBy);
        quizRepository.save(quiz);
    }

    @Override
    public QuizDto getById(Long id) {
        Optional<Quiz> quizOptional = quizRepository.findById(id); // Use standard findById
        if (quizOptional.isPresent()) {
            Quiz quiz = quizOptional.get();

            System.out.println("Quiz retrieved from repository: " + (quiz.getCreatedBy() != null ? quiz.getCreatedBy().getUsername() : null)); // Log at service level

            QuizDto quizDto = quizMapper.toDto(quiz);
            System.out.println("Quiz DTO after mapping: " + quizDto.getCreatedByUsername()); // Check after mapping
            return quizDto;
        } else {
            return null;
        }
    }

    @Override
    public Quiz getByIdInTake(Long id) {
        return quizRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Quiz not found"));
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

    public List<QuizDto> getQuizzesCreatedByTeacher(String teacherUsername) {
        List<Quiz> quizzes = quizRepository.findByCreatedByUsername(teacherUsername);
        return quizzes.stream()
                .map(quiz -> quizMapper.toDto(quiz))
                .collect(Collectors.toList());
    }

    public List<QuizDto> getQuizzesParticipatedByStudent(String studentUsername) {
        List<Quiz> quizzes = quizRepository.findByParticipantsUsername(studentUsername);
        return quizzes.stream()
                .map(quiz -> quizMapper.toDto(quiz))
                .collect(Collectors.toList());
    }

    @Override
    public List<Question> getQuestionsByQuizId(Long quizId) {
        return questionRepository.findByQuizId(quizId);
    }


}