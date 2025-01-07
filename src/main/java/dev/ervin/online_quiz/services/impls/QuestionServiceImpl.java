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
    public QuestionDto create(QuestionDto questionDto) {
        Question question = questionMapper.toEntity(questionDto);
        question.setCreatedAt(LocalDateTime.now());
        question = questionRepository.save(question);
        return questionMapper.toDto(question);
    }

    @Override
    public QuestionDto update(Long id, QuestionDto questionDetails) {
        Question question = questionRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Question not found with ID: " + id));

        question.setQuestion(questionDetails.getQuestion());
        question.setQuestionType(questionDetails.getQuestionType());
        question.setModifiedAt(LocalDateTime.now());

        question = questionRepository.save(question);
        return questionMapper.toDto(question);
    }

    @Override
    public QuestionDto getById(Long id) {
        Question question = questionRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Question not found with ID: " + id));
        return questionMapper.toDto(question);
    }

    @Override
    public List<QuestionDto> getAll() {
        return questionRepository.findAll().stream()
                .map(questionMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public void delete(Long id) {
        Question question = questionRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Question not found with ID: " + id));
        question.setIsDeleted(true);
        question.setModifiedAt(LocalDateTime.now());
        questionRepository.save(question);
    }

    @Override
    public List<QuestionDto> getAllQuestionsByQuiz(Long quizId) {
        return questionRepository.findAllByQuizId(quizId).stream()
                .map(questionMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public void createQuestion(Long quizId, QuestionDto questionDto) {

        Question question = questionMapper.toEntity(questionDto);


        Quiz quiz = quizRepository.findById(quizId)
                .orElseThrow(() -> new EntityNotFoundException("Quiz not found with ID: " + quizId));


        question.setQuiz(quiz);

        question.setCreatedAt(LocalDateTime.now());
        question.setIsDeleted(false);


        questionRepository.save(question);
    }


    @Override
    public List<Answer> getAnswersByQuestionId(Long questionId) {
        return answerRepository.findAllByQuestionId(questionId);
    }
}
