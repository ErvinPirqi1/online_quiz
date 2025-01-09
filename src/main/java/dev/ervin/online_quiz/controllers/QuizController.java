package dev.ervin.online_quiz.controllers;

import dev.ervin.online_quiz.dtos.QuestionDto;
import dev.ervin.online_quiz.dtos.QuizDto;
import dev.ervin.online_quiz.dtos.UserDto;
import dev.ervin.online_quiz.helpers.FileHelper;
import dev.ervin.online_quiz.models.Answer;
import dev.ervin.online_quiz.services.QuestionService;
import dev.ervin.online_quiz.services.QuizService;
import dev.ervin.online_quiz.services.temporary.MockUserService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/quiz")
public class QuizController {

    private final QuizService quizService;
    private final FileHelper fileHelper;
    private final MockUserService mockUserService;
    private final QuestionService questionService;

    public QuizController(QuizService quizService, FileHelper fileHelper, MockUserService mockUserService, QuestionService questionService) {
        this.quizService = quizService;
        this.fileHelper = fileHelper;
        this.mockUserService = mockUserService;
        this.questionService = questionService;
    }

    @GetMapping("")
    public String quiz(Model model) {
        List<QuizDto> quizList = quizService.getAll();
        model.addAttribute("quiz", quizList);
        model.addAttribute("user", mockUserService.getCurrentUser());
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

    @GetMapping("/create/quiz")
    public String createQuizForm(Model model) {
        model.addAttribute("quiz", new QuizDto());
        return "quiz/create/quiz";
    }

    @PostMapping("/create/quiz")
    public String createQuiz(@Valid @ModelAttribute QuizDto quizDto, BindingResult result, RedirectAttributes redirectAttributes,
                             @RequestParam("img") MultipartFile imgFile,
                             @SessionAttribute(value = "user", required = false) UserDto userDto) {

        if (result.hasErrors()) {
            redirectAttributes.addFlashAttribute("errors", result.getAllErrors());
            return "redirect:/quiz/create/quiz";
        }

        if (!imgFile.isEmpty()) {
            try {
                byte[] fileContent = imgFile.getBytes();
                String imagePath = fileHelper.uploadFile("c:/downloads", imgFile.getOriginalFilename(), fileContent);
                quizDto.setImg(imagePath);
            } catch (IOException e) {
                redirectAttributes.addFlashAttribute("error", "Failed to upload image.");
                return "redirect:/quiz/create/quiz";
            }
        } else {
            redirectAttributes.addFlashAttribute("error", "Please upload an image.");
            return "redirect:/quiz/create/quiz";
        }

        // Assign the username (mock user for testing)
        if (userDto != null) {
            quizDto.setCreatedByUsername(userDto.getUsername());
        } else {
            quizDto.setCreatedByUsername("test_user");  // Temporary until authentication is implemented
        }

        // Save the quiz
        quizService.create(quizDto);


        redirectAttributes.addFlashAttribute("message", "Quiz created successfully!");
        return "redirect:/quiz/create/question/" + quizDto.getId();
    }


    @GetMapping("/create/question/{quizId}")
    public String showCreateQuestionForm(@PathVariable Long quizId, Model model) {
        model.addAttribute("quizId", quizId);
        model.addAttribute("question", new QuestionDto());
        return "quiz/create/question";
    }

    @PostMapping("/create/question/{quizId}")
    public String createQuestion(@PathVariable Long quizId, @ModelAttribute QuestionDto questionDto, RedirectAttributes redirectAttributes) {
        questionService.createQuestion(quizId, questionDto);
        redirectAttributes.addFlashAttribute("message", "Question added successfully!");
        return "redirect:/quiz/create/question/" + quizId;
    }

    @PostMapping("/finish/{quizId}")
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
