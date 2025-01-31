package dev.ervin.online_quiz.controllers;

import dev.ervin.online_quiz.dtos.QuestionDto;
import dev.ervin.online_quiz.dtos.QuizDto;
import dev.ervin.online_quiz.dtos.UserDto;
import dev.ervin.online_quiz.helpers.FileHelper;
import dev.ervin.online_quiz.models.Answer;
import dev.ervin.online_quiz.models.Quiz;
import dev.ervin.online_quiz.models.User;
import dev.ervin.online_quiz.services.QuestionService;
import dev.ervin.online_quiz.services.QuizService;
import dev.ervin.online_quiz.services.UserService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/quiz")
public class QuizController {

    private final QuizService quizService;
    private final FileHelper fileHelper;

    private final QuestionService questionService;
    private final UserService userService;

    public QuizController(QuizService quizService, FileHelper fileHelper, QuestionService questionService, UserService userService) {
        this.quizService = quizService;
        this.fileHelper = fileHelper;
        this.questionService = questionService;
        this.userService = userService;
    }

    @GetMapping("")
    public String quiz(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UserDetails userDetails = (UserDetails) auth.getPrincipal();
        UserDto userDto = userService.getUserDetails(userDetails.getUsername());
        model.addAttribute("quiz", quizService.getAll());
        model.addAttribute("user", userDto);
        return "quiz/list";
    }

    @GetMapping("/view/{id}")
    public String getById(@PathVariable Long id, Model model) {
        QuizDto quizDto = quizService.getById(id);
        model.addAttribute("quiz", quizDto);
        return "quiz/view";
    }

    @GetMapping("/details/{id}")
    public String getDetails(@PathVariable Long id, Model model) {
        QuizDto quiz = quizService.getById(id);
        model.addAttribute("quiz", quiz);

        List<QuestionDto> questions = questionService.getAllQuestionsByQuiz(id);
        model.addAttribute("questions", questions);

        Map<Long, List<Answer>> questionAnswersMap = questions.stream()
                .collect(Collectors.toMap(
                        QuestionDto::getId,
                        question -> questionService.getAnswersByQuestionId(question.getId())
                ));
        model.addAttribute("answers", questionAnswersMap);

        return "quiz/details";
    }

    @GetMapping("/createQuiz")
    public String showCreateQuizForm(Model model, @AuthenticationPrincipal UserDetails userDetails) {
        model.addAttribute("quiz", new Quiz());
        if (userDetails != null) {
            model.addAttribute("user", userDetails);
        }
        return "quiz/createQuiz";  // Ensure this matches the Thymeleaf template path
    }

    @PostMapping("/createQuiz")
    public String createQuiz(@Valid @ModelAttribute QuizDto quizDto, BindingResult result, RedirectAttributes redirectAttributes) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UserDetails userDetails = (UserDetails) auth.getPrincipal();
        UserDto userDto = userService.getUserDetails(userDetails.getUsername());

        if (result.hasErrors()) {
            result.getAllErrors().forEach(error -> System.out.println(error.toString()));
            redirectAttributes.addFlashAttribute("errors", result.getAllErrors());
            return "redirect:/quiz";
        }

        if (userDto != null) {
            quizDto.setUserId(userDto.getId());
            quizDto.setCreatedByUsername(userDto.getUsername());
        } else {
            quizDto.setUserId(1L);
            quizDto.setCreatedByUsername("test_user");
        }

        System.out.println("Before saving: " + quizDto);

        // ✅ Get the saved Quiz entity
        Quiz savedQuiz = quizService.create(quizDto);

        System.out.println("After saving: " + savedQuiz);

        // ✅ Use the ID from the entity
        if (savedQuiz.getId() == null) {
            throw new RuntimeException("Quiz ID was not generated after saving!");
        }

        redirectAttributes.addFlashAttribute("message", "Quiz created successfully!");
        return "redirect:/createQuestion/" + savedQuiz.getId();  // ✅ Use entity ID
    }



    @GetMapping("/createQuestion/{id}")
    public String showCreateQuestionForm(@PathVariable Long quizId, Model model) {
        model.addAttribute("quizId", quizId);
        model.addAttribute("questions", new ArrayList<QuestionDto>());
        return "quiz/create/question";
    }


    @PostMapping("/createQuestion/{id}")
    public String createQuestions(@PathVariable Long quizId, @ModelAttribute List<QuestionDto> questions, RedirectAttributes redirectAttributes) {
        try {
            for (QuestionDto questionDto : questions) {
                questionService.createQuestion(quizId, questionDto);
            }
            redirectAttributes.addFlashAttribute("message", "Questions added successfully!");
        } catch (EntityNotFoundException e) {
            redirectAttributes.addFlashAttribute("error", "Quiz not found.");
        }
        return "redirect:/quiz/create/question/" + quizId;
    }

    @PostMapping("/finish/{id}")
    public String finishQuiz(@PathVariable Long quizId, RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("message", "Quiz completed successfully!");
        return "redirect:/quiz/view/" + quizId;
    }

    @PutMapping("/{id}")
    public String updateQuiz(@PathVariable Long id, @Valid @ModelAttribute QuizDto quizDto, BindingResult result,
                             RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            redirectAttributes.addFlashAttribute("errors", result.getAllErrors());
            return "redirect:/quiz/edit/" + id;
        }

        quizService.update(id, quizDto);
        redirectAttributes.addFlashAttribute("message", "Quiz updated successfully!");
        return "redirect:/quiz/view/" + id;
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        quizService.delete(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/edit/{id}")
    public String editQuizForm(@PathVariable Long id, Model model) {
        QuizDto quizDto = quizService.getById(id);
        model.addAttribute("quiz", quizDto);
        return "quiz/edit";
    }
}
