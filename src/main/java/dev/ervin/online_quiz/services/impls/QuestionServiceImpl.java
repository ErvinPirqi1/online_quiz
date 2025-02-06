package dev.ervin.online_quiz.services.impls;

import dev.ervin.online_quiz.dtos.AnswerDto;
import dev.ervin.online_quiz.dtos.QuestionDto;
import dev.ervin.online_quiz.mappers.QuestionMapper;
import dev.ervin.online_quiz.models.Answer;
import dev.ervin.online_quiz.models.Question;
import dev.ervin.online_quiz.models.Quiz;
import dev.ervin.online_quiz.models.User;
import dev.ervin.online_quiz.repositories.AnswerRepository;
import dev.ervin.online_quiz.repositories.QuestionRepository;
import dev.ervin.online_quiz.repositories.QuizRepository;
import dev.ervin.online_quiz.repositories.UserRepository;
import dev.ervin.online_quiz.services.QuestionService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class QuestionServiceImpl implements QuestionService {

    private final QuestionRepository questionRepository;
    private final AnswerRepository answerRepository;
    private final QuestionMapper questionMapper;
    private final QuizRepository quizRepository;
    private final UserRepository userRepository;

    public QuestionServiceImpl(QuestionRepository questionRepository,
                               AnswerRepository answerRepository,
                               QuestionMapper questionMapper, QuizRepository quizRepository, UserRepository userRepository) {
        this.questionRepository = questionRepository;
        this.answerRepository = answerRepository;
        this.questionMapper = questionMapper;
        this.quizRepository = quizRepository;
        this.userRepository = userRepository;
    }

    @Override
    public List<QuestionDto> getAllQuestionsByQuiz(Long quizId) {
        List<Question> questions = questionRepository.findByQuizId(quizId); // Assuming you have this method in your repository
        return questions.stream()
                .map(questionMapper::toDto) // Use your mapper!
                .collect(Collectors.toList());
    }

    @Override
    public List<Answer> getAnswersByQuestionId(Long questionId) {
        return answerRepository.findByQuestionId(questionId);
    }

    @Override
    public void createQuestion(Long quizId, QuestionDto questionDto, String username) {
        Quiz quiz = quizRepository.findById(quizId)
                .orElseThrow(() -> new EntityNotFoundException("Quiz not found with ID: " + quizId));

        User createdBy = userRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("User not found with username: " + username));

        Question question = new Question();
        question.setQuiz(quiz);
        question.setQuestion(questionDto.getQuestionText());
        question.setQuestionType(questionDto.getQuestionType());
        question.setCreatedAt(LocalDateTime.now());
        question.setIsDeleted(false);
        question.setCreatedBy(createdBy);
        question.setUser(createdBy);

        questionRepository.save(question);

        if (questionDto.getAnswers() != null) {
            for (AnswerDto answerDto : questionDto.getAnswers()) {
                Answer answer = new Answer();
                answer.setQuestion(question);
                answer.setOptionText(answerDto.getText());
                answer.setIsCorrect(answerDto.getIsCorrect() != null ? answerDto.getIsCorrect() : false); // 🔥 Ensure isCorrect is never null
                answer.setCreatedAt(LocalDateTime.now());
                answer.setIsDeleted(false);
                answer.setCreatedBy(createdBy);

                answerRepository.save(answer);
            }
        }



    }


    @Override
    public QuestionDto create(QuestionDto entity) {
        return null;
    }

    @Override
    public QuestionDto getById(Long questionId) {
        Question question = questionRepository.findById(questionId).orElseThrow(() -> new EntityNotFoundException("Question not found"));
        return questionMapper.mapToDtoWithQuizId(question); // Use the new mapping method
    }

    @Override
    public QuestionDto update(Long questionId, QuestionDto questionDto) {
        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new EntityNotFoundException("Question not found"));

        question.setQuestion(questionDto.getQuestionText());
        question.setQuestionType(questionDto.getQuestionType());
        question.setModifiedAt(LocalDateTime.now());
        question.setModifiedBy(getCurrentUser());


        List<Answer> existingAnswers = answerRepository.findByQuestionId(questionId);
        Map<Long, Answer> existingAnswerMap = existingAnswers.stream()
                .collect(Collectors.toMap(Answer::getId, Function.identity()));

        List<AnswerDto> answerDtos = questionDto.getAnswers() != null ? questionDto.getAnswers() : new ArrayList<>();

        for (int i = 0; i < answerDtos.size(); i++) {
            AnswerDto answerDto = answerDtos.get(i);
            Answer answer;

            if (answerDto.getId() != null && existingAnswerMap.containsKey(answerDto.getId())) {
                // Update existing answer
                answer = existingAnswerMap.get(answerDto.getId());
                answer.setOptionText(answerDto.getText());
                answer.setIsCorrect(answerDto.getIsCorrect() != null ? answerDto.getIsCorrect() : false);
                answer.setModifiedAt(LocalDateTime.now());
                answer.setModifiedBy(getCurrentUser());
                existingAnswerMap.remove(answerDto.getId());
            } else {

                answer = new Answer();
                answer.setQuestion(question);
                answer.setOptionText(answerDto.getText());
                answer.setIsCorrect(answerDto.getIsCorrect() != null ? answerDto.getIsCorrect() : false);
                answer.setCreatedAt(LocalDateTime.now());
                answer.setCreatedBy(getCurrentUser());
            }
            answerRepository.save(answer);
        }

        // Delete any remaining answers in the map (those not in the updated list)
        existingAnswerMap.values().forEach(answerRepository::delete);


        Question updatedQuestion = questionRepository.save(question);
        return questionMapper.toDto(updatedQuestion);
    }



    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof UserDetails) {
            String username = ((UserDetails) authentication.getPrincipal()).getUsername();
            return userRepository.findByUsername(username)
                    .orElseThrow(() -> new EntityNotFoundException("User not found: " + username));
        }
        return null; // Or throw an exception if user is required
    }


    @Override
    public List<QuestionDto> getAll() {
        return List.of();
    }

    @Override
    public void delete(Long aLong) {

    }
}
