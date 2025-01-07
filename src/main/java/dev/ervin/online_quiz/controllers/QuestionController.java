package dev.ervin.online_quiz.controllers;

import dev.ervin.online_quiz.dtos.QuestionDto;
import dev.ervin.online_quiz.dtos.QuizDto;
import dev.ervin.online_quiz.models.Answer;
import dev.ervin.online_quiz.services.QuestionService;
import dev.ervin.online_quiz.services.QuizService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/question")
public class QuestionController {

    private final QuizService quizService;
    private final QuestionService questionService;

    public QuestionController(QuestionService questionService, QuizService quizService) {
        this.questionService = questionService;
        this.quizService = quizService;
    }

    // Get details of a quiz with its questions and answers


    // Show form to create a question
    @GetMapping("/create/{quizId}")
    public String showCreateQuestionForm(@PathVariable Long quizId, Model model) {
        model.addAttribute("quizId", quizId);
        model.addAttribute("question", new QuestionDto());
        return "quiz/create/question";
    }

    // Handle question creation
    @PostMapping("/create/{quizId}")
    public String createQuestion(@PathVariable Long quizId, @ModelAttribute QuestionDto questionDto) {
        questionService.createQuestion(quizId, questionDto);
        return "redirect:/quiz/" + quizId;
    }
}
