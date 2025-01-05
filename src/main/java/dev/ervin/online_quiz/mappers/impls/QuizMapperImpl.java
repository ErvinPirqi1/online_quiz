package dev.ervin.online_quiz.mappers.impls;

import dev.ervin.online_quiz.dtos.QuizDto;
import dev.ervin.online_quiz.mappers.QuizMapper;
import dev.ervin.online_quiz.models.Quiz;
import dev.ervin.online_quiz.models.User;

public class QuizMapperImpl implements QuizMapper {

    @Override
    public Quiz toEntity(QuizDto quizDto) {
        Quiz quiz = new Quiz();
        quiz.setId(quizDto.getId());

        // Assuming that the user ID in QuizDto is to be set to the 'user' field in Quiz entity
        // You might want to map the User entity properly if you have access to it
        User user = new User();  // Or fetch the User object based on the userId
        user.setId(quizDto.getUserId());
        quiz.setUser(user);

        // Assuming the same for createdBy and modifiedBy
        User createdBy = new User();
        createdBy.setId(quizDto.getCreatedById());
        quiz.setCreatedBy(createdBy);

        if (quizDto.getModifiedById() != null) {
            User modifiedBy = new User();
            modifiedBy.setId(quizDto.getModifiedById());
            quiz.setModifiedBy(modifiedBy);
        }

        quiz.setTitle(quizDto.getTitle());
        quiz.setDescription(quizDto.getDescription());
        quiz.setImg(quizDto.getImg());
        quiz.setCategory(quizDto.getCategory());
        quiz.setVisibility(quizDto.getVisibility());
        quiz.setCreatedAt(quizDto.getCreatedAt());
        quiz.setModifiedAt(quizDto.getModifiedAt());
        quiz.setIsDeleted(quizDto.getIsDeleted());

        return quiz;
    }

    @Override
    public QuizDto toDto(Quiz quiz) {
        QuizDto quizDto = new QuizDto();
        quizDto.setId(quiz.getId());

        // Assuming that the user ID in Quiz entity is to be set to the 'userId' field in QuizDto
        if (quiz.getUser() != null) {
            quizDto.setUserId(quiz.getUser().getId());
        }

        // Assuming the same for createdBy and modifiedBy
        if (quiz.getCreatedBy() != null) {
            quizDto.setCreatedById(quiz.getCreatedBy().getId());
        }
        if (quiz.getModifiedBy() != null) {
            quizDto.setModifiedById(quiz.getModifiedBy().getId());
        }

        quizDto.setTitle(quiz.getTitle());
        quizDto.setDescription(quiz.getDescription());
        quizDto.setImg(quiz.getImg());
        quizDto.setCategory(quiz.getCategory());
        quizDto.setVisibility(quiz.getVisibility());
        quizDto.setCreatedAt(quiz.getCreatedAt());
        quizDto.setModifiedAt(quiz.getModifiedAt());
        quizDto.setIsDeleted(quiz.getIsDeleted());

        return quizDto;
    }
}

