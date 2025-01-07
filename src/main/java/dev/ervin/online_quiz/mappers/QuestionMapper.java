package dev.ervin.online_quiz.mappers;

import dev.ervin.online_quiz.infrastructure.mapping.SimpleMapper;
import dev.ervin.online_quiz.models.Question;
import dev.ervin.online_quiz.dtos.QuestionDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring") // Use Spring to automatically inject the mapper
public interface QuestionMapper extends SimpleMapper<Question,QuestionDto> {
}
