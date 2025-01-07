package dev.ervin.online_quiz.mappers;

import dev.ervin.online_quiz.dtos.QuizDto;
import dev.ervin.online_quiz.infrastructure.mapping.SimpleMapper;
import dev.ervin.online_quiz.models.Quiz;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface QuizMapper {



    @Mapping(source = "createdBy.username", target = "createdByUsername") // Map the username from User
    QuizDto toDto(Quiz quiz);


    @Mapping(source = "createdByUsername", target = "createdBy.username") // Reverse mapping (if needed for save)
    Quiz toEntity(QuizDto quizDto);



}
