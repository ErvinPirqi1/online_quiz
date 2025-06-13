package dev.ervin.online_quiz.mappers.impls;

import dev.ervin.online_quiz.dtos.AnswerDto;
import dev.ervin.online_quiz.models.Answer;

// AnswerMapper.java
public class AnswerMapper {

    public static AnswerDto toDto(Answer answer) {
        if (answer == null) return null;
        return new AnswerDto(
                answer.getId(),
                answer.getOptionText(),  // map "optionText" → "text"
                answer.getIsCorrect()
        );
    }
}

