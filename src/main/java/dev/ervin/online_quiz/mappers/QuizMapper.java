package dev.ervin.online_quiz.mappers;

import dev.ervin.online_quiz.dtos.QuizDto;
import dev.ervin.online_quiz.models.Quiz;
import dev.ervin.online_quiz.models.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.stereotype.Component;

@Mapper(componentModel = "spring")
@Component
public interface QuizMapper {
    Quiz toEntity(QuizDto quizDto);
//    QuizDto toDto(Quiz quiz);

    @Mapping(source = "createdBy.username", target = "createdByUsername") // Ensure the mapping is correct
    QuizDto toDto(Quiz quiz);

    default String mapUserToUsername(User user) {
        return user != null ? user.getUsername() : null;
    }

    default Quiz mapToEntity(QuizDto quizDto) {
        Quiz quiz = new Quiz();
        // ... other mappings
        if (quizDto.getCreatedByUsername() != null) {
            User createdBy = new User();
            createdBy.setUsername(quizDto.getCreatedByUsername());
            quiz.setCreatedBy(createdBy);
        }
        return quiz;
    }

}
