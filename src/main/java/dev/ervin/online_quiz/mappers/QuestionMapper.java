package dev.ervin.online_quiz.mappers;

import dev.ervin.online_quiz.dtos.QuestionDto;
import dev.ervin.online_quiz.models.Question;

public interface QuestionMapper {
    QuestionDto toDto(Question question);
    Question toEntity(QuestionDto questionDto);

    QuestionDto mapToDtoWithQuizId(Question question);
}