package dev.ervin.online_quiz.services.impls;

import dev.ervin.online_quiz.dtos.QuizDto;
import dev.ervin.online_quiz.models.Quiz;
import dev.ervin.online_quiz.models.User;
import dev.ervin.online_quiz.repositories.QuizRepository;
import dev.ervin.online_quiz.repositories.UserRepository;
import dev.ervin.online_quiz.services.QuizService;
import dev.ervin.online_quiz.mappers.QuizMapper;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class QuizServiceImpl implements QuizService {

    private final UserRepository userRepository;
    private final QuizRepository quizRepository;
    private final QuizMapper quizMapper;  // Assuming you have a QuizMapper to map between Quiz and QuizDto

    @Autowired
    public QuizServiceImpl(UserRepository userRepository, QuizRepository quizRepository, QuizMapper quizMapper) {
        this.userRepository = userRepository;
        this.quizRepository = quizRepository;
        this.quizMapper = quizMapper;
    }

    public Quiz create(QuizDto quizDto) {
        Quiz quiz = quizMapper.toEntity(quizDto);

        User createdBy = userRepository.findByUsername(quizDto.getCreatedByUsername())
                .orElseThrow(() -> new RuntimeException("User not found with username: " + quizDto.getCreatedByUsername()));

        User user = userRepository.findById(quizDto.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + quizDto.getUserId()));

        quiz.setCreatedBy(createdBy);
        quiz.setUser(user);

        // ✅ Save the quiz and return the saved entity
        return quizRepository.save(quiz);
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

    public List<QuizDto> getQuizzesCreatedByTeacher(String teacherUsername) {
        List<Quiz> quizzes = quizRepository.findByCreatedByUsername(teacherUsername);
        return quizzes.stream()
                .map(quiz -> quizMapper.toDto(quiz)) // Assuming you have a method to convert Quiz to QuizDto
                .collect(Collectors.toList());
    }

    public List<QuizDto> getQuizzesParticipatedByStudent(String studentUsername) {
        List<Quiz> quizzes = quizRepository.findByParticipantsUsername(studentUsername);
        return quizzes.stream()
                .map(quiz -> quizMapper.toDto(quiz))
                .collect(Collectors.toList());
    }




}

