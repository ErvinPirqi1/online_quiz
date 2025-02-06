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

@RestController
@RequestMapping("/api/quizzes")
@CrossOrigin(origins = "*") // Adjust for production
public class QuizApiController {

    @Autowired
    private QuizService quizService;

    @Autowired
    private QuestionService questionService;

    @Autowired
    private TestResultService testResultService;

    @Autowired
    private StudentAnswerService studentAnswerService;

    @Autowired
    private AnswerRepository answerRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    // Get all quizzes
    @GetMapping("")
    public ResponseEntity<List<QuizDto>> getAllQuizzes() {
        return ResponseEntity.ok(quizService.getAll());
    }

    // Get quiz by ID
    @GetMapping("/{id}")
    public ResponseEntity<QuizDto> getQuiz(@PathVariable Long id) {
        QuizDto quiz = quizService.getById(id);
        if (quiz == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(quiz);
    }

    // Create new quiz
    @PostMapping("")
    public ResponseEntity<?> createQuiz(@RequestBody @Valid QuizDto quizDto, @AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails == null) return ResponseEntity.status(401).body("Unauthorized");

        User creator = userService.getUserByUsername(userDetails.getUsername());
        Quiz quiz = new Quiz();
        quiz.setCategory(quizDto.getCategory());
        quiz.setDescription(quizDto.getDescription());
        quiz.setTitle(quizDto.getTitle());
        quiz.setVisibility(quizDto.getVisibility());
        quiz.setStatus(quizDto.getStatus());
        quiz.setCreatedBy(creator);
        quiz.setUser(creator);

        Quiz savedQuiz = quizService.create(quiz);
        return ResponseEntity.ok(savedQuiz.getId());
    }

    // Update quiz
    @PutMapping("/{id}")
    public ResponseEntity<?> updateQuiz(@PathVariable Long id, @RequestBody @Valid QuizDto quizDto, @AuthenticationPrincipal UserDetails userDetails) {
        QuizDto existing = quizService.getById(id);
        if (existing == null) return ResponseEntity.notFound().build();

        existing.setTitle(quizDto.getTitle());
        existing.setDescription(quizDto.getDescription());
        existing.setCategory(quizDto.getCategory());
        existing.setVisibility(quizDto.getVisibility());
        existing.setStatus(quizDto.getStatus());
        existing.setModifiedByUsername(userDetails.getUsername());

        quizService.update(id, existing);
        return ResponseEntity.ok("Updated successfully");
    }

    // Toggle visibility
    @PostMapping("/{id}/toggleVisibility")
    public ResponseEntity<?> toggleVisibility(@PathVariable Long id, @AuthenticationPrincipal UserDetails userDetails) {
        UserDto user = userService.getUserDetails(userDetails.getUsername());
        QuizDto quiz = quizService.getById(id);
        quiz.setModifiedByUsername(user.getUsername());
        quizService.toggleVisibility(id, quiz);
        return ResponseEntity.ok("Visibility toggled");
    }

    // Create questions
    @PostMapping("/{id}/questions")
    public ResponseEntity<?> createQuestions(@PathVariable("id") Long quizId, @RequestBody QuestionForm questionForm, @AuthenticationPrincipal UserDetails userDetails) {
        if (questionForm == null || questionForm.getQuestions() == null) return ResponseEntity.badRequest().body("Invalid question form");

        for (QuestionDto questionDto : questionForm.getQuestions()) {
            if (questionDto != null && questionDto.getQuestionText() != null) {
                questionService.createQuestion(quizId, questionDto, userDetails.getUsername());
            }
        }
        return ResponseEntity.ok("Questions created");
    }

    // Get quiz details with questions and answers
    @GetMapping("/{id}/details")
    public ResponseEntity<?> getQuizDetails(@PathVariable Long id) {
        QuizDto quiz = quizService.getById(id);
        if (quiz == null) return ResponseEntity.notFound().build();

        List<QuestionDto> questions = questionService.getAllQuestionsByQuiz(quiz.getId());
        Map<Long, List<Answer>> answers = questions.stream()
                .collect(Collectors.toMap(QuestionDto::getId, q -> questionService.getAnswersByQuestionId(q.getId())));

        return ResponseEntity.ok(Map.of(
                "quiz", quiz,
                "questions", questions,
                "answers", answers
        ));
    }

    // Start quiz (initialize TestResult)
    @PostMapping("/start/{quizId}")
    public ResponseEntity<?> startQuiz(@PathVariable Long quizId, @AuthenticationPrincipal UserDetails userDetails) {
        Quiz quiz = quizService.getByIdInTake(quizId);
        List<Question> questions = quizService.getQuestionsByQuizId(quizId);
        Collections.shuffle(questions);

        User user = userRepository.findByUsername(userDetails.getUsername()).orElse(null);

        TestResult testResult = new TestResult();
        testResult.setQuiz(quiz);
        testResult.setUser(user);
        testResult.setScore(BigDecimal.ZERO);
        testResult.setCreatedAt(LocalDateTime.now());
        testResult.setCurrentQuestionIndex(0);

        TestResult saved = testResultService.save(testResult);
        return ResponseEntity.ok(Map.of("testResultId", saved.getId()));
    }

    // Get current question for quiz-taking
    @GetMapping("/take/{testResultId}")
    public ResponseEntity<?> takeQuiz(@PathVariable Long testResultId) {
        TestResult testResult = testResultService.getById(testResultId);
        List<Question> questions = quizService.getQuestionsByQuizId(testResult.getQuiz().getId());

        int index = testResult.getCurrentQuestionIndex();
        if (index >= questions.size()) return ResponseEntity.ok(Map.of("completed", true));

        Question current = questions.get(index);
        List<Answer> answers = answerRepository.findByQuestionId(current.getId());
        Collections.shuffle(answers);

        return ResponseEntity.ok(Map.of(
                "testResult", testResult,
                "question", current,
                "answers", answers
        ));
    }

    // Submit an answer
    @PostMapping("/submitAnswer/{testResultId}")
    public ResponseEntity<?> submitAnswer(@PathVariable Long testResultId, @RequestParam Long selectedAnswerId) {
        TestResult testResult = testResultService.getById(testResultId);
        List<Question> questions = quizService.getQuestionsByQuizId(testResult.getQuiz().getId());

        int currentQuestionIndex = testResult.getCurrentQuestionIndex();
        Question currentQuestion = questions.get(currentQuestionIndex);
        Answer selectedAnswer = answerRepository.findById(selectedAnswerId).orElse(null);

        StudentAnswer studentAnswer = new StudentAnswer();
        studentAnswer.setTestResult(testResult);
        studentAnswer.setQuestion(currentQuestion);
        studentAnswer.setSelectedOption(selectedAnswer);
        studentAnswer.setIsCorrect(selectedAnswer != null && selectedAnswer.getIsCorrect());
        studentAnswer.setCreatedAt(LocalDateTime.now());

        studentAnswerService.save(studentAnswer);

        if (studentAnswer.getIsCorrect()) {
            testResult.setScore(testResult.getScore().add(BigDecimal.ONE));
        }

        testResult.setCurrentQuestionIndex(currentQuestionIndex + 1);
        testResultService.update(testResult);

        return ResponseEntity.ok(Map.of("nextQuestionIndex", testResult.getCurrentQuestionIndex()));
    }

    // Get result
    @GetMapping("/result/{testResultId}")
    public ResponseEntity<?> getResult(@PathVariable Long testResultId) {
        TestResult testResult = testResultService.getById(testResultId);
        return ResponseEntity.ok(testResult);
    }

    // Delete quiz
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteQuiz(@PathVariable Long id) {
        quizService.delete(id);
        return ResponseEntity.ok("Quiz deleted");
    }
}
