package dev.ervin.online_quiz.services;


import dev.ervin.online_quiz.models.Quiz;


import java.util.List;

public interface QuizService extends BaseService<Quiz, Long> {
//    List<Quiz> getAllQuizzesByUser(Long userId);

    List<Quiz> getRecentQuizzes();

}

