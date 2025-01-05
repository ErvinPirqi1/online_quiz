package dev.ervin.online_quiz.controllers;

import dev.ervin.online_quiz.models.Answer;
import dev.ervin.online_quiz.models.Question;
import dev.ervin.online_quiz.models.Quiz;
import dev.ervin.online_quiz.repositories.AnswerRepository;
import dev.ervin.online_quiz.repositories.QuestionRepository;
import dev.ervin.online_quiz.services.QuestionService;
import dev.ervin.online_quiz.services.QuizService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/question")
public class QuestionController {

    private final QuizService quizService;
    private final QuestionRepository questionRepository;
    private final QuestionService questionService;
    private final AnswerRepository answerRepository;


    public QuestionController(QuestionService questionService, QuizService quizService, QuestionRepository questionRepository, AnswerRepository answerRepository) {
        this.questionService = questionService;
        this.quizService = quizService;
        this.questionRepository = questionRepository;
        this.answerRepository = answerRepository;
    }

    @GetMapping("/details/{id}")
    public String getDetails(@PathVariable Long id, Model model) {
        // Fetch the quiz by id
        var quiz = quizService.getById(id);
        model.addAttribute("quiz", quiz);

        // Fetch all questions related to the quiz
        List<Question> questions = questionRepository.findAllByQuizId(id);
        model.addAttribute("question", questions);

        // Fetch answers for each question and attach them
        Map<Long, List<Answer>> questionAnswersMap = new HashMap<>();
        for (Question question : questions) {
            List<Answer> answers = answerRepository.findAllByQuestionId(question.getId());
            questionAnswersMap.put(question.getId(), answers);
        }
        model.addAttribute("answersMap", questionAnswersMap);

        return "question/details";
    }


    @GetMapping("/question/create")
    public String showCreateQuestionForm(@PathVariable Long quizId, Model model) {
        model.addAttribute("quizId", quizId);
        var question = questionService.getAllQuestionsByQuiz(quizId);
        model.addAttribute("question", question);
        return "quiz/create/question";
    }

    @PostMapping("/question/create")
    public String createQuestion(@PathVariable Long quizId, @ModelAttribute Question question) {
        questionService.createQuestion(quizId, question);
        return "redirect:/quiz/" + quizId;
    }
}