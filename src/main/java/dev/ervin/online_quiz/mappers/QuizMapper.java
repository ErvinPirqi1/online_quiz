package dev.ervin.online_quiz.mappers;

import dev.ervin.online_quiz.dtos.QuizDto;
import dev.ervin.online_quiz.models.Quiz;
import dev.ervin.online_quiz.models.User;
//import org.mapstruct.Mapper;
//import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
//import org.springframework.context.annotation.Primary;
//import org.springframework.stereotype.Component;
//@Component
//@Mapper(componentModel = "spring")
//@Primary
public interface QuizMapper {
//    QuizMapper INSTANCE = Mappers.getMapper(QuizMapper.class);

//    // Parameterless constructor
//    default QuizMapper() {
//        // No-op
//    }
//    @Mapping(target = "createdBy.id", source = "createdBy")
    Quiz toEntity(QuizDto quizDto);
//    QuizDto toDto(Quiz quiz);

//    @Mapping(source = "createdBy.username", target = "createdByUsername") // Ensure the mapping is correct
//    @Mapping(source = "createdBy", target = "createdBy.id")
    QuizDto toDto(Quiz quiz);


//    default String mapUserToUsername(User user) {
//        return user != null ? user.getUsername() : null;
//    }
//
//    default Quiz mapToEntity(QuizDto quizDto) {
//        Quiz quiz = new Quiz();
//        // ... other mappings
//        if (quizDto.getCreatedByUsername() != null) {
//            User createdBy = new User();
//            createdBy.setUsername(quizDto.getCreatedByUsername());
//            quiz.setCreatedBy(createdBy);
//        }
//        return quiz;
//    }

}
