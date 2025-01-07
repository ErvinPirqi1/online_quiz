package dev.ervin.online_quiz.services;

import dev.ervin.online_quiz.dtos.QuizDto;

import java.util.List;

public interface QuizService {
    QuizDto create(QuizDto quizDto);  // Returns a QuizDto

    QuizDto update(Long id, QuizDto quizDto);  // Returns a QuizDto

    QuizDto getById(Long id);  // Returns QuizDto

    List<QuizDto> getAll();  // Returns List<QuizDto>

    List<QuizDto> getRecentQuizzes();  // Custom method for quizzes

    void delete(Long id);
}


