package dev.ervin.online_quiz.services;

import dev.ervin.online_quiz.models.Answer;

import java.util.List;

public interface AnswerService extends BaseService<Answer, Long> {
    List<Answer> getAnswerOptionsByQuestion(Long questionId);
}

