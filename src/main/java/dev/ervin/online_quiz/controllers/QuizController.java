package dev.ervin.online_quiz.controllers;

import dev.ervin.online_quiz.dtos.QuizDto;
import dev.ervin.online_quiz.dtos.UserDto;
import dev.ervin.online_quiz.helpers.FileHelper;
import dev.ervin.online_quiz.mappers.QuizMapper;
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

import java.io.IOException;
import java.util.List;

@Controller
@RequestMapping("/quiz")
public class QuizController {

    private final QuizService quizService;
    private final FileHelper fileHelper;
    private final QuizMapper quizMapper;

    public QuizController(QuizService quizService, FileHelper fileHelper, QuizMapper quizMapper) {
        this.quizService = quizService;
        this.fileHelper = fileHelper;
        this.quizMapper = quizMapper;
    }

    @GetMapping("")
    public String quiz(Model model) {
        List<QuizDto> quizList = quizService.getAll();
        model.addAttribute("quiz", quizList);
        return "quiz/list";
    }

    @GetMapping("/view/{id}")
    public String getById(@PathVariable Long id, Model model) {
        QuizDto quizDto = quizService.getById(id);
        model.addAttribute("quiz", quizDto);
        return "quiz/view";
    }

    @GetMapping("/create")
    public String createQuizForm(Model model) {
        model.addAttribute("quiz", new QuizDto());
        return "quiz/create";
    }

    @PostMapping
    public String createQuiz(@Valid @ModelAttribute QuizDto quizDto, BindingResult result, RedirectAttributes redirectAttributes,
                             @RequestParam("img") MultipartFile imgFile,
                             @SessionAttribute("user") UserDto userDto) {

        if (result.hasErrors()) {
            redirectAttributes.addFlashAttribute("errors", result.getAllErrors());
            return "redirect:/quiz/create";
        }

        if (!imgFile.isEmpty()) {
            try {
                byte[] fileContent = imgFile.getBytes();
                String imagePath = fileHelper.uploadFile("c:/downloads", imgFile.getOriginalFilename(), fileContent);
                quizDto.setImg(imagePath);
            } catch (IOException e) {
                redirectAttributes.addFlashAttribute("error", "Failed to upload image.");
                return "redirect:/quiz/create";
            }
        } else {
            redirectAttributes.addFlashAttribute("error", "Please upload an image.");
            return "redirect:/quiz/create";
        }

        quizDto.setCreatedByUsername(userDto.getUsername());

        // Pass the quizDto to the service
        quizService.create(quizDto);  // This is correct, pass the DTO here

        redirectAttributes.addFlashAttribute("message", "Quiz created successfully!");
        return "redirect:/quiz";
    }




    @PutMapping("/{id}")
    public String updateQuiz(@PathVariable Long id, @Valid @ModelAttribute QuizDto quizDto, BindingResult result,
                             RedirectAttributes redirectAttributes) {

        if (result.hasErrors()) {
            // Handle validation errors
            redirectAttributes.addFlashAttribute("errors", result.getAllErrors());
            return "redirect:/quiz/edit/" + id; // Redirect to the edit form with errors
        }

        // Update the quiz using DTO
        quizService.update(id, quizDto);
        redirectAttributes.addFlashAttribute("message", "Quiz updated successfully!");
        return "redirect:/quiz/view/" + id; // Redirect to the quiz view page
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        quizService.delete(id);  // Call delete method from the service
        return ResponseEntity.ok().build();
    }

    @GetMapping("/edit/{id}")
    public String editQuizForm(@PathVariable Long id, Model model) {
        QuizDto quizDto = quizService.getById(id);  // Fetch QuizDto to pre-populate the form
        model.addAttribute("quiz", quizDto);
        return "quiz/edit";  // Render the edit form
    }
}
