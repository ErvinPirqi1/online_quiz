package dev.ervin.online_quiz.services.impls;

import dev.ervin.online_quiz.models.Answer;
import dev.ervin.online_quiz.repositories.AnswerRepository;
import dev.ervin.online_quiz.services.AnswerService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AnswerServiceImpl implements AnswerService {

    private final AnswerRepository answerOptionRepository;

    public AnswerServiceImpl(AnswerRepository answerOptionRepository) {
        this.answerOptionRepository = answerOptionRepository;
    }

    @Override
    public Answer create(Answer answerOption) {
        return answerOptionRepository.save(answerOption);
    }

    @Override
    public Answer update(Long id, Answer answerOptionDetails) {
        Answer answerOption = answerOptionRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Answer option not found with ID: " + id));
        answerOption.setOptionText(answerOptionDetails.getOptionText());
        answerOption.setIsCorrect(answerOptionDetails.getIsCorrect());
        return answerOptionRepository.save(answerOption);
    }

    @Override
    public Answer getById(Long id) {
        return answerOptionRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Answer option not found with ID: " + id));
    }

    @Override
    public List<Answer> getAll() {
        return answerOptionRepository.findAll();
    }

    @Override
    public void delete(Long id) {
        Answer answerOption = getById(id);
        answerOption.setIsDeleted(true);
        answerOptionRepository.save(answerOption);
    }

    @Override
    public List<Answer> getAnswerOptionsByQuestion(Long questionId) {
        return answerOptionRepository.findAllByQuestionId(questionId);
    }
}
