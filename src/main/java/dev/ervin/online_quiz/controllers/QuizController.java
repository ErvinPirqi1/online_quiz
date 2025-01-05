package dev.ervin.online_quiz.controllers;

import dev.ervin.online_quiz.dtos.UserDto;
import dev.ervin.online_quiz.helpers.FileHelper;
import dev.ervin.online_quiz.models.Quiz;
import dev.ervin.online_quiz.services.QuizService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/quiz")
public class QuizController {

    private final QuizService quizService;
    private final FileHelper fileHelper;

    public QuizController(QuizService quizService, FileHelper fileHelper) {
        this.quizService = quizService;
        this.fileHelper = fileHelper;
    }

    @GetMapping("")
    public String quiz(Model model) {
        List<Quiz> quiz = quizService.getAll();
        model.addAttribute("quiz", quiz);
        return "quiz/list";
    }

    @GetMapping("/view/{id}")
    public String getById(@PathVariable Long id ,Model model) {
        var quiz = quizService.getById(id);
        model.addAttribute("quiz", quiz);
        return "quiz/view";
    }

    @PostMapping
    public Quiz createQuiz(@Valid @ModelAttribute Quiz quiz, BindingResult result, RedirectAttributes redirectAttributes,
                           @RequestParam("img")MultipartFile imgFile,
                           @SessionAttribute("user") UserDto userDto) {




        return quizService.create(quiz);
    }

    @PutMapping("/{id}")
    public Quiz update(@PathVariable Long id, @RequestBody Quiz quiz) {
        return quizService.update(id, quiz);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        quizService.delete(id);
        return ResponseEntity.ok().build();
    }
}

