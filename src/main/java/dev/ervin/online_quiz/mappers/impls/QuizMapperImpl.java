package dev.ervin.online_quiz.mappers.impls;


import dev.ervin.online_quiz.dtos.QuizDto;
import dev.ervin.online_quiz.mappers.QuizMapper;
import dev.ervin.online_quiz.models.Quiz;
import dev.ervin.online_quiz.models.User;
import org.springframework.stereotype.Component;

@Component
public class QuizMapperImpl implements QuizMapper {
    public Quiz toEntity(QuizDto quizDto) {
        if (quizDto == null) {
            return null;
        }

        Quiz quiz = new Quiz();
        quiz.setTitle(quizDto.getTitle());
        quiz.setDescription(quizDto.getDescription());
        quiz.setCategory(quizDto.getCategory());
        quiz.setVisibility(quizDto.getVisibility());
        quiz.setId(quizDto.getId());
        quiz.setStatus(quizDto.getStatus());
        quiz.setModifiedAt(quizDto.getModifiedAt());

        if (quizDto.getCreatedByUsername() != null) {
            User createdBy = new User();
            createdBy.setUsername(quizDto.getCreatedByUsername());
            quiz.setCreatedBy(createdBy);
        } //No other way to map the User object without a service or repository to retrieve it

        return quiz;
    }

    public QuizDto toDto(Quiz quiz) {
        if (quiz == null) {
            return null;
        }

        QuizDto quizDto = new QuizDto();
        quizDto.setTitle(quiz.getTitle());
        quizDto.setDescription(quiz.getDescription());
        quizDto.setCategory(quiz.getCategory());
        quizDto.setVisibility(quiz.getVisibility());
        quizDto.setId(quiz.getId());
        quizDto.setStatus(quiz.getStatus());
        quizDto.setModifiedByUsername(quiz.getModifiedBy() != null ? quiz.getModifiedBy().getUsername() : null);
        quizDto.setCreatedByUsername(quiz.getCreatedBy() != null ? quiz.getCreatedBy().getUsername() : null);

        // **Add these lines:**
        if (quiz.getCreatedBy() != null) {
            quizDto.setCreatedBy(quiz.getCreatedBy().getId());  // Assuming createdBy is a Long user id
            quizDto.setUserId(quiz.getCreatedBy().getId());     // If userId means owner id (createdBy)
        }

        return quizDto;
    }


}
