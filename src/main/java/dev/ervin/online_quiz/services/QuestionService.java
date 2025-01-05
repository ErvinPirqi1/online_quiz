package dev.ervin.online_quiz.services;

import dev.ervin.online_quiz.models.Question;

import java.util.List;


public interface QuestionService extends BaseService<Question, Long> {
    List<Question> getAllQuestionsByQuiz(Long quizId);

    void createQuestion(Long quizId, Question question);

}

