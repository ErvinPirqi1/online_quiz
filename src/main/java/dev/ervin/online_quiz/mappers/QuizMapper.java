package dev.ervin.online_quiz.mappers;

import dev.ervin.online_quiz.dtos.QuizDto;
import dev.ervin.online_quiz.infrastructure.mapping.SimpleMapper;
import dev.ervin.online_quiz.models.Quiz;

public interface QuizMapper extends SimpleMapper<Quiz, QuizDto> {

}
