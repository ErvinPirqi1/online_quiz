package dev.ervin.online_quiz.mappers;

import dev.ervin.online_quiz.dtos.QuizDto;
import dev.ervin.online_quiz.models.Quiz;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface QuizMapper {
    Quiz toEntity(QuizDto quizDto);
    QuizDto toDto(Quiz quiz);
}
