package dev.ervin.online_quiz.controllers;

import dev.ervin.online_quiz.helpers.ListPartitioner;
import dev.ervin.online_quiz.models.Quiz;
import dev.ervin.online_quiz.services.QuizService;
import dev.ervin.online_quiz.services.impls.QuizServiceImpl;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.ArrayList;
import java.util.List;

@Controller
public class HomeController {

    private final QuizService quizService;

    public HomeController(QuizServiceImpl quizServiceImpl) {
        this.quizService = quizServiceImpl;
    }

    @GetMapping("/")
    public String home(Model model) {
        List<Quiz> quizzes = quizService.getRecentQuizzes();
        if (quizzes == null) {
            quizzes = new ArrayList<>();
        }
        List<List<Quiz>> quizPartitions = ListPartitioner.partition(quizzes, 3);
        model.addAttribute("quizPartitions", quizPartitions);

        return "index";
    }
}
