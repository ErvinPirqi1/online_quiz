package dev.ervin.online_quiz.controllers;

import dev.ervin.online_quiz.dtos.QuestionDto;
import dev.ervin.online_quiz.dtos.QuizDto;
import dev.ervin.online_quiz.dtos.UserDto;
import dev.ervin.online_quiz.helpers.FileHelper;
import dev.ervin.online_quiz.helpers.QuestionForm;
import dev.ervin.online_quiz.models.*;
import dev.ervin.online_quiz.repositories.AnswerRepository;
import dev.ervin.online_quiz.repositories.QuizRepository;
import dev.ervin.online_quiz.repositories.UserRepository;
import dev.ervin.online_quiz.services.*;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;


import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/quiz")
public class QuizController {

    @Autowired
    private QuizService quizService;

    @Autowired
    private TestResultService testResultService;

    @Autowired
    private StudentAnswerService studentAnswerService;

    @Autowired
    private AnswerRepository answerRepository; // Inject AnswerRepository

    @Autowired
    private UserRepository userRepository;

    private final FileHelper fileHelper;
    private final QuizRepository quizRepository;
    private final QuestionService questionService;
    private final UserService userService;

    public QuizController(QuizService quizService, FileHelper fileHelper, QuizRepository quizRepository, QuestionService questionService, UserService userService) {
        this.quizService = quizService;
        this.fileHelper = fileHelper;
        this.quizRepository = quizRepository;
        this.questionService = questionService;
        this.userService = userService;
    }

    @ModelAttribute("quiz")
    public QuizDto loadQuiz(@PathVariable(required = false) Long id) {
        System.out.println("loadQuiz method called!"); // Log for @ModelAttribute execution
        if (id != null) {
            return quizService.getById(id);
        }
        return null;
    }

    @ModelAttribute("isQuizCreator")
    public boolean isQuizCreator(@AuthenticationPrincipal UserDetails userDetails, @ModelAttribute("quiz") QuizDto quiz) {
        System.out.println("isQuizCreator method called!"); // Log inside isQuizCreator

        if (userDetails == null || quiz == null || quiz.getCreatedByUsername() == null) { // Corrected null check!
            System.out.println("isQuizCreator: userDetails or quiz is null");
            return false;
        }

        String loggedInUsername = userDetails.getUsername();
        String quizCreatorUsername = quiz.getCreatedByUsername();

        System.out.println("isQuizCreator: comparing " + quizCreatorUsername + " and " + loggedInUsername);

        return quizCreatorUsername.equals(loggedInUsername);
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
    public String viewQuiz(@PathVariable Long id, Model model, @AuthenticationPrincipal UserDetails userDetails) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        userDetails = (auth != null && auth.getPrincipal() instanceof UserDetails) ? (UserDetails) auth.getPrincipal() : null;

        QuizDto quiz = quizService.getById(id);
        if (quiz == null) {
            return "error/quizNotFound";
        }
        model.addAttribute("quiz", quiz);

        if (userDetails != null && quiz.getCreatedByUsername() != null && quiz.getCreatedByUsername().equals(userDetails.getUsername())) {
            model.addAttribute("isQuizCreator", true);
        } else {
            model.addAttribute("isQuizCreator", false);
        }

        return "quiz/view";
    }


    @GetMapping("/details/{id}")
    @PreAuthorize("hasPermission(#quiz?.id, 'UPDATE')") // Corrected EL
    public String getDetails(@PathVariable Long id, Model model) { // Removed @ModelAttribute and added @PathVariable
        if (model.getAttribute("quiz") == null) {
            return "error/quizNotFound"; // Handle quiz not found
        }

        QuizDto quiz = (QuizDto) model.getAttribute("quiz");

        List<QuestionDto> questions = questionService.getAllQuestionsByQuiz(quiz.getId());
        model.addAttribute("questions", questions);

        Map<Long, List<Answer>> questionAnswersMap = questions.stream()
                .collect(Collectors.toMap(
                        QuestionDto::getId,
                        question -> questionService.getAnswersByQuestionId(question.getId())
                ));
        model.addAttribute("answers", questionAnswersMap);

        return "quiz/details";
    }

//    @GetMapping("/testQuiz")
//    public String testQuiz(Model model) {
//        QuizDto quiz = quizService.getById(1L); // Replace 1L with a valid quiz ID
//        if (quiz == null) {
//            model.addAttribute("quizNotFound", true); // Add a flag to the model
//        } else {
//            model.addAttribute("quiz", quiz);
//        }
//        return "quiz/testView"; // Create a new test view
//    }

    @GetMapping("/createQuiz")
    public String showCreateQuizForm(Model model, @AuthenticationPrincipal UserDetails userDetails) {
        model.addAttribute("quiz", new Quiz());
        if (userDetails != null) {
            model.addAttribute("user", userDetails);
        }
        return "quiz/createQuiz";
    }

    @PostMapping("/createQuiz")
    public String createQuiz(@Valid @ModelAttribute QuizDto quizDto, BindingResult result, RedirectAttributes redirectAttributes, @AuthenticationPrincipal UserDetails userDetails) {

        if (result.hasErrors()) {
            redirectAttributes.addFlashAttribute("errors", result.getAllErrors());
            return "redirect:/quiz/createQuiz"; // Redirect back to the form
        }

        if (userDetails == null) {
            redirectAttributes.addFlashAttribute("error", "You must be logged in to create a quiz.");
            return "redirect:/quiz/createQuiz"; // Redirect back to the form
        }

        User creator = userService.getUserByUsername(userDetails.getUsername());
        if (creator == null) {
            redirectAttributes.addFlashAttribute("error", "User not found.");
            return "redirect:/quiz/createQuiz";
        }

        Quiz quiz = new Quiz();
        quiz.setCategory(quizDto.getCategory());
        quiz.setDescription(quizDto.getDescription());
        quiz.setTitle(quizDto.getTitle());
        quiz.setVisibility(quizDto.getVisibility());
        quiz.setStatus(quizDto.getStatus());
        quiz.setCreatedBy(creator); // Set the User entity
        quiz.setUser(creator); // Set the User entity (if you have a separate 'user' field)

        System.out.println("Before saving: " + quiz);
        Quiz savedQuiz = quizService.create(quiz); // Pass the Quiz entity
        System.out.println("After saving: " + savedQuiz);

        if (savedQuiz == null || savedQuiz.getId() == null) {
            redirectAttributes.addFlashAttribute("error", "Failed to create quiz.");
            return "redirect:/quiz/createQuiz";
        }

        redirectAttributes.addFlashAttribute("message", "Quiz created successfully!");
        return "redirect:/quiz/createQuestion/" + savedQuiz.getId();
    }


    @GetMapping("/createQuestion/{id}")  // *** CRITICAL: Initialization ***
    public String showCreateQuestionForm(@PathVariable("id") Long id, Model model) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UserDetails userDetails = (UserDetails) auth.getPrincipal();
        UserDto userDto = userService.getUserDetails(userDetails.getUsername());

        model.addAttribute("quizId", id);
        model.addAttribute("user", userDto);

        QuestionForm questionForm = new QuestionForm();
        questionForm.setQuestions(new ArrayList<>()); // Initialize the questions list!

        // *** VERY IMPORTANT: Initialize at least ONE QuestionDto and its answers list ***
        QuestionDto firstQuestion = new QuestionDto();
        firstQuestion.setAnswers(new ArrayList<>()); // Initialize the answers list for the first question!
        questionForm.getQuestions().add(firstQuestion); // Add the first question to the list

        model.addAttribute("questionForm", questionForm); // Add the QuestionForm to the model
        return "quiz/createQuestion";
    }

    @GetMapping("/edit/{id}")
    @PreAuthorize("hasPermission(#id, 'UPDATE')") // Check UPDATE permission
    public String editQuizForm(@PathVariable Long id, Model model) {
        QuizDto quizDto = quizService.getById(id);
        model.addAttribute("quiz", quizDto); // Make sure you have the quiz DTO in the model
        return "quiz/edit"; // Return the name of your Thymeleaf template
    }

    @PostMapping("/updateQuiz/{id}")
    @PreAuthorize("hasPermission(#id, 'UPDATE')") // Check UPDATE permission
    public String updateQuiz(@PathVariable Long id, @Valid @ModelAttribute QuizDto quizDto, BindingResult result, RedirectAttributes redirectAttributes) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UserDetails userDetails = (UserDetails) auth.getPrincipal();
        UserDto userDto1 = userService.getUserDetails(userDetails.getUsername());

        if (result.hasErrors()) {
            redirectAttributes.addFlashAttribute("errors", result.getAllErrors());
            return "redirect:/quiz/edit/" + id;
        }

        QuizDto existingQuiz = quizService.getById(id);

        if (existingQuiz == null) {
            return "error/quizNotFound";
        }

        existingQuiz.setTitle(quizDto.getTitle());
        existingQuiz.setDescription(quizDto.getDescription());
        existingQuiz.setCategory(quizDto.getCategory());
        existingQuiz.setVisibility(quizDto.getVisibility());
        existingQuiz.setStatus(quizDto.getStatus());
        existingQuiz.setModifiedByUsername(userDto1.getUsername()); // Set the username in the DTO

        quizService.update(id, existingQuiz); // Update the quiz

        redirectAttributes.addFlashAttribute("message", "Quiz updated successfully!");
        return "redirect:/quiz/view/" + id;
    }

    @GetMapping("/toggleVisibility/{id}")
    @PreAuthorize("hasPermission(#id, 'TOGGLE_VISIBILITY')")
    public String toggleVisibility(@PathVariable Long id, @ModelAttribute QuizDto quizDto, RedirectAttributes redirectAttributes) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UserDetails userDetails = (UserDetails) auth.getPrincipal();
        UserDto user = userService.getUserDetails(userDetails.getUsername());

        quizDto.setModifiedByUsername(user.getUsername());
        quizService.toggleVisibility(id, quizDto); // Pass quizDto to the service

        redirectAttributes.addFlashAttribute("message", "Quiz visibility toggled.");
        return "redirect:/quiz/view/" + id;
    }



    @PostMapping("createQuestion/{id}")
    public String createQuestions(@PathVariable("id") Long quizId, @ModelAttribute QuestionForm questionForm, RedirectAttributes redirectAttributes) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            UserDetails userDetails = (UserDetails) auth.getPrincipal();
            String username = userDetails.getUsername(); // Get current username

            System.out.println("QuestionForm received: " + questionForm);

            if (questionForm != null && questionForm.getQuestions() != null) {
                for (QuestionDto questionDto : questionForm.getQuestions()) {
                    if (questionDto != null && questionDto.getQuestionText() != null) {
                        questionService.createQuestion(quizId, questionDto, username);
                    } else {
                        System.out.println("Skipping Question: " + questionDto + " because it is null or questionText is null");
                    }
                }
            } else {
                System.out.println("QuestionForm or questions list is null!");
            }
        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("error", "An error occurred: " + e.getMessage());
            return "redirect:/quiz/question/create/" + quizId;
        }
        return "redirect:/quiz/view/" + quizId;
    }

    @GetMapping("/editQuestion/{id}")
    public String editQuestion(@PathVariable Long id, Model model) {
        QuestionDto questionDto = questionService.getById(id);
        model.addAttribute("question", questionDto);
        model.addAttribute("quizId", questionDto.getQuizId()); // Add quizId to the model
        return "/quiz/editQuestion";
    }

    // ... (Rest of the controller code remains the same)
    @PostMapping("/update/{id}")
    public String updateQuestion(@PathVariable Long id, @ModelAttribute QuestionDto questionDto, Model model) {
        Long quizId = questionService.getById(id).getQuizId();
        questionService.update(id, questionDto);
        return "redirect:/quiz/details/" + quizId;
    }

    @GetMapping("/start/{quizId}")
    public String startQuiz(@PathVariable Long quizId, HttpSession session, @AuthenticationPrincipal UserDetails userDetails) {
        Quiz quiz = quizService.getByIdInTake(quizId);
        List<Question> questions = quizService.getQuestionsByQuizId(quizId);
        Collections.shuffle(questions);

        User user = userRepository.findByUsername(userDetails.getUsername()).orElse(null);

        TestResult testResult = new TestResult();
        testResult.setQuiz(quiz);
        testResult.setUser(user);
        testResult.setScore(BigDecimal.ZERO);
        testResult.setCreatedAt(LocalDateTime.now());
        testResult.setCurrentQuestionIndex(0); // Initialize

        testResult = testResultService.save(testResult);
        session.setAttribute("testResult", testResult);

        return "redirect:/quiz/take/" + testResult.getId();
    }

    @GetMapping("/take/{testResultId}")
    public String takeQuiz(@PathVariable Long testResultId, HttpSession session, Model model) {
        TestResult testResult = (TestResult) session.getAttribute("testResult");
        if (testResult == null) {
            return "redirect:/";
        }

        List<Question> questions = quizService.getQuestionsByQuizId(testResult.getQuiz().getId());
        int currentQuestionIndex = testResult.getCurrentQuestionIndex();

        if (currentQuestionIndex >= questions.size()) {
            return "redirect:/quiz/result/" + testResultId;
        }

        Question currentQuestion = questions.get(currentQuestionIndex);
        List<Answer> answers = answerRepository.findByQuestionId(currentQuestion.getId());
        Collections.shuffle(answers);

        model.addAttribute("testResult", testResult);
        model.addAttribute("question", currentQuestion);
        model.addAttribute("answers", answers);

        return "quiz/take";
    }

    @PostMapping("/submitAnswer/{id}") // Correct @PostMapping
    public String submitAnswer(@PathVariable Long id, @RequestParam Long selectedAnswerId, HttpSession session) {
        TestResult testResult = (TestResult) session.getAttribute("testResult");
        if (testResult == null) {
            return "redirect:/";
        }

        List<Question> questions = quizService.getQuestionsByQuizId(testResult.getQuiz().getId());
        int currentQuestionIndex = testResult.getCurrentQuestionIndex();
        Question currentQuestion = questions.get(currentQuestionIndex);

        Answer selectedAnswer = answerRepository.findById(selectedAnswerId).orElse(null);

        StudentAnswer studentAnswer = new StudentAnswer();
        studentAnswer.setTestResult(testResult);
        studentAnswer.setQuestion(currentQuestion);
        studentAnswer.setSelectedOption(selectedAnswer);
        studentAnswer.setIsCorrect(selectedAnswer != null && selectedAnswer.getIsCorrect());

        studentAnswer.setCreatedAt(LocalDateTime.now()); // Set the createdAt!
        // Set other audit fields if you have them (createdBy, modifiedAt, etc.)

        studentAnswerService.save(studentAnswer);

        if (studentAnswer.getIsCorrect()) {
            testResult.setScore(testResult.getScore().add(BigDecimal.ONE));
        }

        testResult.setCurrentQuestionIndex(currentQuestionIndex + 1);
        testResultService.update(testResult);

        return "redirect:/quiz/take/" + id;
    }

    @GetMapping("/result/{id}")
    public String result(@PathVariable Long id, Model model) {
        TestResult testResult = testResultService.getById(id);
        model.addAttribute("testResult", testResult);
        return "quiz/result";
    }
}


//    @DeleteMapping("/{id}")
//    public ResponseEntity<?> delete(@PathVariable Long id) {
//        quizService.delete(id);
//        return ResponseEntity.ok().build();
//    }

//    @GetMapping("/edit/{id}")
//    public String editQuizForm(@PathVariable Long id, Model model) {
//        QuizDto quizDto = quizService.getById(id);
//        model.addAttribute("quiz", quizDto);
//        return "quiz/list";
//    }

