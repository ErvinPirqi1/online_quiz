package dev.ervin.online_quiz.controllers;

import dev.ervin.online_quiz.dtos.QuizDto;
import dev.ervin.online_quiz.helpers.ListPartitioner;
import dev.ervin.online_quiz.services.QuizService;
import dev.ervin.online_quiz.services.impls.QuizServiceImpl;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
public class HomeController {

    private final QuizService quizService;

    public HomeController(QuizServiceImpl quizServiceImpl) {
        this.quizService = quizServiceImpl;
    }

    // GET /api/home
    @GetMapping("/api/home")
    public ResponseEntity<?> getHomeData() {
        List<QuizDto> quizzes = quizService.getRecentQuizzes();

        List<List<QuizDto>> quizPartitions = (quizzes == null || quizzes.isEmpty())
                ? new ArrayList<>()
                : ListPartitioner.partition(quizzes, 3);

        return ResponseEntity.ok(quizPartitions);
    }
}
