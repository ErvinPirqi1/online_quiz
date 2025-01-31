package dev.ervin.online_quiz.services.impls;

import dev.ervin.online_quiz.dtos.QuestionDto;
import dev.ervin.online_quiz.mappers.QuestionMapper;
import dev.ervin.online_quiz.models.Answer;
import dev.ervin.online_quiz.models.Question;
import dev.ervin.online_quiz.models.Quiz;
import dev.ervin.online_quiz.repositories.AnswerRepository;
import dev.ervin.online_quiz.repositories.QuestionRepository;
import dev.ervin.online_quiz.repositories.QuizRepository;
import dev.ervin.online_quiz.services.QuestionService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class QuestionServiceImpl implements QuestionService {

    private final QuestionRepository questionRepository;
    private final AnswerRepository answerRepository;
    private final QuestionMapper questionMapper;
    private final QuizRepository quizRepository;

    public QuestionServiceImpl(QuestionRepository questionRepository,
                               AnswerRepository answerRepository,
                               QuestionMapper questionMapper, QuizRepository quizRepository) {
        this.questionRepository = questionRepository;
        this.answerRepository = answerRepository;
        this.questionMapper = questionMapper;
        this.quizRepository = quizRepository;
    }

    @Override
    public List<QuestionDto> getAllQuestionsByQuiz(Long quizId) {
        return List.of();
    }

    @Override
    public void createQuestion(Long quizId, QuestionDto questionDto) {
        Quiz quiz = quizRepository.findById(quizId)
                .orElseThrow(() -> new EntityNotFoundException("Quiz not found with ID: " + quizId));

        Question question = questionMapper.toEntity(questionDto);
        question.setQuiz(quiz);
        question.setCreatedAt(LocalDateTime.now());
        question.setIsDeleted(false);

        questionRepository.save(question);

        // Save the answers for the question
        if (questionDto.getOptions() != null) {
            for (int i = 0; i < questionDto.getOptions().size(); i++) {
                Answer answer = new Answer();
                answer.setOptionText(questionDto.getOptions().get(i).getText());
                answer.setIsCorrect(questionDto.getCorrectAnswer() != null && questionDto.getCorrectAnswer().equals(i));
                answer.setQuestion(question);
                answer.setCreatedAt(LocalDateTime.now());
                answer.setIsDeleted(false);

                answerRepository.save(answer);
            }
        }
    }

    @Override
    public List<Answer> getAnswersByQuestionId(Long questionId) {
        return List.of();
    }

    @Override
    public QuestionDto create(QuestionDto entity) {
        return null;
    }

    @Override
    public QuestionDto update(Long aLong, QuestionDto entityDetails) {
        return null;
    }

    @Override
    public QuestionDto getById(Long aLong) {
        return null;
    }

    @Override
    public List<QuestionDto> getAll() {
        return List.of();
    }

    @Override
    public void delete(Long aLong) {

    }
}
