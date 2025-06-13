package dev.ervin.online_quiz.controllers;

import dev.ervin.online_quiz.dtos.*;
import dev.ervin.online_quiz.helpers.QuestionForm;
import dev.ervin.online_quiz.mappers.impls.AnswerMapper;
import dev.ervin.online_quiz.models.*;
import dev.ervin.online_quiz.repositories.AnswerRepository;
import dev.ervin.online_quiz.repositories.UserRepository;
import dev.ervin.online_quiz.services.*;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/quizzes")
@CrossOrigin(origins = "http://localhost:5173") // Adjust for production
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
    @GetMapping("/{quizId}")
    public ResponseEntity<QuizDto> getQuiz(@PathVariable Long quizId) {
        QuizDto quiz = quizService.getById(quizId);
        if (quiz == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(quiz);
    }

    @PostMapping("")
    public ResponseEntity<?> createQuiz(@RequestBody @Valid CreateQuizDto dto,
                                        @AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized");
        }

        Optional<User> optionalUser = userRepository.findByUsername(userDetails.getUsername());
        if (optionalUser.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not found");
        }
        User currentUser = optionalUser.get();

        Quiz quiz = new Quiz();
        quiz.setTitle(dto.getTitle());
        quiz.setDescription(dto.getDescription());
        quiz.setCategory(dto.getCategory());
        quiz.setVisibility(dto.getVisibility());
        quiz.setStatus((short) 1); // or dto.getStatus() if you want to allow it

        quiz.setUser(currentUser);
        quiz.setCreatedBy(currentUser);

        Quiz savedQuiz = quizService.create(quiz);  // Use your service here

        return ResponseEntity.ok(savedQuiz.getId());
    }



    // Update quiz
    @PutMapping("/{quizId}")
    public ResponseEntity<?> updateQuiz(@PathVariable Long quizId, @RequestBody @Valid QuizDto quizDto, @AuthenticationPrincipal UserDetails userDetails) {
        QuizDto existing = quizService.getById(quizId);
        if (existing == null) return ResponseEntity.notFound().build();

        existing.setTitle(quizDto.getTitle());
        existing.setDescription(quizDto.getDescription());
        existing.setCategory(quizDto.getCategory());
        existing.setVisibility(quizDto.getVisibility());
        existing.setStatus(quizDto.getStatus());
        existing.setModifiedByUsername(userDetails.getUsername());

        quizService.update(quizId, existing);
        return ResponseEntity.ok("Updated successfully");
    }

    @PutMapping("/{quizId}/questions/{questionId}")
    public ResponseEntity<?> updateQuestionWithAnswers(
            @PathVariable Long quizId,
            @PathVariable Long questionId,
            @RequestBody QuestionDto questionDto,
            @AuthenticationPrincipal UserDetails userDetails) {

        User currentUser = userService.getUserByUsername(userDetails.getUsername());
        if (currentUser == null) {
            return ResponseEntity.status(401).body("Unauthorized");
        }

        boolean updated = questionService.updateQuestionWithAnswers(questionId, questionDto, currentUser);
        if (!updated) {
            return ResponseEntity.status(404).body("Question not found or update failed");
        }

        return ResponseEntity.ok("Question and answers updated successfully");
    }

    // Toggle visibility of a quiz
    @PostMapping("/{quizId}/visibility/toggle")
    public ResponseEntity<?> toggleVisibility(@PathVariable Long quizId, @AuthenticationPrincipal UserDetails userDetails) {
        UserDto user = userService.getUserDetails(userDetails.getUsername());
        QuizDto quiz = quizService.getById(quizId);
        if (quiz == null) return ResponseEntity.notFound().build();

        quiz.setModifiedByUsername(user.getUsername());
        quizService.toggleVisibility(quizId, quiz);
        return ResponseEntity.ok("Visibility toggled");
    }

    @PostMapping("/{quizId}/questions")
    public ResponseEntity<?> createQuestions(@PathVariable Long quizId, @RequestBody QuestionForm questionForm, @AuthenticationPrincipal UserDetails userDetails) {
        if (questionForm == null || questionForm.getQuestions() == null) {
            return ResponseEntity.badRequest().body("Invalid question form");
        }

        User creator = userService.getUserByUsername(userDetails.getUsername());
        Quiz quiz = quizService.getByIdInTake(quizId);

        if (quiz == null || !quiz.getUser().getId().equals(creator.getId())) {
            return ResponseEntity.status(403).body("Only quiz creator can add questions.");
        }

        for (QuestionDto questionDto : questionForm.getQuestions()) {
            if (questionDto != null && questionDto.getQuestionText() != null) {
                questionService.createQuestion(quizId, questionDto, userDetails.getUsername());
            }
        }
        return ResponseEntity.ok("Questions created");
    }


    @GetMapping("/{quizId}/details")
    public ResponseEntity<?> getQuizDetails(@PathVariable Long quizId, @AuthenticationPrincipal UserDetails userDetails) {
        System.out.println("=== getQuizDetails called ===");
        System.out.println("Path variable quizId: " + quizId);
        System.out.println("Authenticated userDetails: " + userDetails);

        if (userDetails == null) {
            System.out.println("UserDetails is null - Unauthorized");
            return ResponseEntity.status(401).body("Unauthorized - no user details");
        }

        QuizDto quiz = quizService.getById(quizId);
        System.out.println("Quiz fetched from service: " + quiz);
        if (quiz == null) {
            System.out.println("Quiz not found with id: " + quizId);
            return ResponseEntity.notFound().build();
        }

        User currentUser = userService.getUserByUsername(userDetails.getUsername());
        if (currentUser == null) {
            System.out.println("Current user not found by username: " + userDetails.getUsername());
            return ResponseEntity.status(401).body("Unauthorized - user not found");
        }
        System.out.println("Current user from DB: " + currentUser.getUsername() + ", id: " + currentUser.getId());

        boolean isOwner = quiz.getUserId() != null && quiz.getUserId().equals(currentUser.getId());
        boolean isAdmin = hasRole(userDetails, "ADMIN");
        System.out.println("Authorization check: isOwner=" + isOwner + ", isAdmin=" + isAdmin);

        if (!isOwner && !isAdmin) {
            System.out.println("Access denied: user is neither owner nor admin");
            return ResponseEntity.status(403).body("Forbidden");
        }

        List<QuestionDto> questions = questionService.getAllQuestionsByQuiz(quiz.getId());
        System.out.println("Number of questions fetched: " + (questions == null ? 0 : questions.size()));

        for (QuestionDto question : questions) {
            List<Answer> answers = questionService.getAnswersByQuestionId(question.getId());
            List<AnswerDto> answerDtos = answers.stream()
                    .map(AnswerMapper::toDto)
                    .collect(Collectors.toList());
            question.setAnswers(answerDtos);
        }

        System.out.println("Returning quiz details with questions and answers");
        return ResponseEntity.ok(Map.of(
                "quiz", quiz,
                "questions", questions
        ));
    }




    // Get questions by quiz ID
    @GetMapping("/{quizId}/questions")
    public ResponseEntity<?> getQuestionsByQuizId(@PathVariable Long quizId) {
        List<QuestionDto> questions = questionService.getAllQuestionsByQuiz(quizId);
        return ResponseEntity.ok(questions);
    }


    // Start a quiz (initialize a test result)
    @PostMapping("/{quizId}/start")
    public ResponseEntity<?> startQuiz(@PathVariable Long quizId, @AuthenticationPrincipal UserDetails userDetails) {
        Quiz quiz = quizService.getByIdInTake(quizId);
        if (quiz == null) return ResponseEntity.notFound().build();

        User user = userRepository.findByUsername(userDetails.getUsername()).orElse(null);
        if (user == null) return ResponseEntity.status(401).body("Unauthorized");

        TestResult testResult = new TestResult();
        testResult.setQuiz(quiz);
        testResult.setUser(user);
        testResult.setScore(BigDecimal.ZERO);
        testResult.setCreatedAt(LocalDateTime.now());
        testResult.setCurrentQuestionIndex(0);

        TestResult saved = testResultService.save(testResult);
        return ResponseEntity.ok(Map.of("testResultId", saved.getId()));
    }

    // Get current question for taking the quiz
    @GetMapping("/take/{testResultId}/question")
    public ResponseEntity<?> getCurrentQuestion(@PathVariable Long testResultId) {
        TestResult testResult = testResultService.getById(testResultId);
        if (testResult == null) return ResponseEntity.notFound().build();

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

    // Submit an answer for current question
    @PostMapping("/take/{testResultId}/submit")
    public ResponseEntity<?> submitAnswer(@PathVariable Long testResultId, @RequestParam Long selectedAnswerId) {
        TestResult testResult = testResultService.getById(testResultId);
        if (testResult == null) return ResponseEntity.notFound().build();

        List<Question> questions = quizService.getQuestionsByQuizId(testResult.getQuiz().getId());

        int currentQuestionIndex = testResult.getCurrentQuestionIndex();
        if (currentQuestionIndex >= questions.size()) {
            return ResponseEntity.badRequest().body("Quiz already completed");
        }

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

    // Get quiz test result
    @GetMapping("/result/{testResultId}")
    public ResponseEntity<?> getResult(@PathVariable Long testResultId) {
        TestResult testResult = testResultService.getById(testResultId);
        if (testResult == null) return ResponseEntity.notFound().build();

        return ResponseEntity.ok(testResult);
    }

    // Delete quiz by ID
    @DeleteMapping("/{quizId}")
    public ResponseEntity<?> deleteQuiz(@PathVariable Long quizId) {
        quizService.delete(quizId);
        return ResponseEntity.ok("Quiz deleted");
    }

    @GetMapping("/my")
    public ResponseEntity<List<QuizDto>> getMyQuizzes(@AuthenticationPrincipal UserDetails userDetails) {
        System.out.println("User details: " + userDetails);
        if (userDetails == null) {
            return ResponseEntity.status(401).build();
        }
        String username = userDetails.getUsername();
        List<QuizDto> quizzes = quizService.getQuizzesCreatedByUser(username);
        return ResponseEntity.ok(quizzes);
    }

    // Get quizzes participated in by the logged-in user
    @GetMapping("/participated")
    public ResponseEntity<List<QuizDto>> getParticipatedQuizzes(@AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails == null) {
            return ResponseEntity.status(401).build();
        }
        String username = userDetails.getUsername();
        List<QuizDto> quizzes = quizService.getQuizzesParticipatedByUser(username);
        return ResponseEntity.ok(quizzes);
    }

    // Get quizzes neither created by nor participated in by the logged-in user
    @GetMapping("/other")
    public ResponseEntity<List<QuizDto>> getOtherQuizzes(@AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails == null) {
            return ResponseEntity.status(401).build();
        }
        String username = userDetails.getUsername();
        List<QuizDto> quizzes = quizService.getQuizzesNotCreatedOrParticipatedByUser(username);
        return ResponseEntity.ok(quizzes);
    }

    private boolean hasRole(UserDetails userDetails, String role) {
        return userDetails.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_" + role));
    }


}
