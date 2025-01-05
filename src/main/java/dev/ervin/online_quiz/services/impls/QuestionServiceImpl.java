package dev.ervin.online_quiz.services.impls;

import dev.ervin.online_quiz.models.Question;
import dev.ervin.online_quiz.repositories.QuestionRepository;
import dev.ervin.online_quiz.services.QuestionService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class QuestionServiceImpl implements QuestionService {

    private final QuestionRepository questionRepository;

    public QuestionServiceImpl(QuestionRepository questionRepository) {
        this.questionRepository = questionRepository;
    }

    @Override
    public Question create(Question question) {
        return questionRepository.save(question);
    }

    @Override
    public Question update(Long id, Question questionDetails) {
        Question question = questionRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Question not found with ID: " + id));
        question.setQuestion(questionDetails.getQuestion());
        question.setQuestionType(questionDetails.getQuestionType());
        return questionRepository.save(question);
    }

    @Override
    public Question getById(Long id) {
        return questionRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Question not found with ID: " + id));
    }

    @Override
    public List<Question> getAll() {
        return questionRepository.findAll();
    }

    @Override
    public void delete(Long id) {
        Question question = getById(id);
        question.setIsDeleted(true);
        questionRepository.save(question);
    }

    @Override
    public List<Question> getAllQuestionsByQuiz(Long quizId) {
        return questionRepository.findAllByQuizId(quizId);
    }

    @Override
    public void createQuestion(Long quizId, Question question) {

    }

}

