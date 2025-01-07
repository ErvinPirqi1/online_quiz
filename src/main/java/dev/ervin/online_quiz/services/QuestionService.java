package dev.ervin.online_quiz.services;

import dev.ervin.online_quiz.dtos.QuestionDto;
import dev.ervin.online_quiz.models.Answer;

import java.util.List;

public interface QuestionService extends BaseService<QuestionDto, Long> {
    List<QuestionDto> getAllQuestionsByQuiz(Long quizId);

    void createQuestion(Long quizId, QuestionDto questionDto);

    List<Answer> getAnswersByQuestionId(Long questionId);
}
