package dev.ervin.online_quiz.mappers;

import dev.ervin.online_quiz.infrastructure.mapping.SimpleMapper;
import dev.ervin.online_quiz.models.Question;
import dev.ervin.online_quiz.dtos.QuestionDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface QuestionMapper {

    QuestionDto toDto(Question question);

    Question toEntity(QuestionDto questionDto);

    @Mapping(target = "quizId", source = "question.quiz.id") // Add this if you have the Quiz relationship
    QuestionDto mapToDtoWithQuizId(Question question);
}
