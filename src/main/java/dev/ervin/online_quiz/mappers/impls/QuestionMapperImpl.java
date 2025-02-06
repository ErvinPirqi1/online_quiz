package dev.ervin.online_quiz.mappers.impls;

import dev.ervin.online_quiz.dtos.QuestionDto;
import dev.ervin.online_quiz.mappers.QuestionMapper;
import dev.ervin.online_quiz.models.Question;
import org.springframework.stereotype.Component;

@Component
public class QuestionMapperImpl implements QuestionMapper {

    @Override
    public QuestionDto toDto(Question question) {
        if (question == null) return null;

        QuestionDto dto = new QuestionDto();
        dto.setId(question.getId());
        dto.setQuizId(question.getQuiz() != null ? question.getQuiz().getId() : null);
        dto.setQuestionText(question.getQuestion());
        dto.setQuestionType(question.getQuestionType());
        return dto;
    }

    @Override
    public Question toEntity(QuestionDto dto) {
        if (dto == null) return null;

        Question question = new Question();
        question.setId(dto.getId());
        question.setQuestion(dto.getQuestionText());
        question.setQuestionType(dto.getQuestionType());
        return question;
    }

    @Override
    public QuestionDto mapToDtoWithQuizId(Question question) {
        return null;
    }
}
