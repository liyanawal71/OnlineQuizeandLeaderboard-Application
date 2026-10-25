
package com.quizchamp;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("/api/student/quiz")
public class StudentQuizSubmissionController {

    private final QuizQuestionService questionService;
    private final QuizResultService quizResultService;
    private final UserAccountRepository userAccountRepository;
    private final QuizTopicRepository topicRepository;

    public StudentQuizSubmissionController(
            QuizQuestionService questionService,
            QuizResultService quizResultService,
            UserAccountRepository userAccountRepository,
            QuizTopicRepository topicRepository) {

        this.questionService = questionService;
        this.quizResultService = quizResultService;
        this.userAccountRepository = userAccountRepository;
        this.topicRepository = topicRepository;
    }

    @PostMapping("/submit")
    public ResponseEntity<?> submitQuiz(
            @RequestBody QuizSubmissionRequest request,
            Authentication authentication) {

        if (authentication == null
                || !authentication.isAuthenticated()
                || "anonymousUser".equals(authentication.getName())) {

            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Please log in first."));
        }

        if (request == null
                || request.topicId() == null
                || request.answers() == null
                || request.answers().isEmpty()) {

            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Please submit your answers."));
        }

        try {
            List<QuizQuestion> questions =
                    questionService.getQuestionsByTopic(request.topicId());

            if (questions.isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "This quiz has no questions."));
            }

            Set<Long> questionIds = new HashSet<>();

            for (AnswerSubmission answer : request.answers()) {
                if (answer == null
                        || answer.questionId() == null
                        || answer.selectedOption() == null
                        || !List.of("A", "B", "C", "D")
                                .contains(answer.selectedOption().toUpperCase())) {

                    return ResponseEntity.badRequest()
                            .body(Map.of("error", "An answer is invalid."));
                }

                if (!questionIds.add(answer.questionId())) {
                    return ResponseEntity.badRequest()
                            .body(Map.of("error", "Duplicate answers were submitted."));
                }
            }

            if (request.answers().size() != questions.size()) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error",
                                "Please answer every question in this quiz."));
            }

            int score = 0;

            for (QuizQuestion question : questions) {
                AnswerSubmission submittedAnswer = request.answers()
                        .stream()
                        .filter(answer ->
                                answer.questionId().equals(question.getId()))
                        .findFirst()
                        .orElse(null);

                if (submittedAnswer == null) {
                    return ResponseEntity.badRequest()
                            .body(Map.of("error",
                                    "An answer is missing for a question."));
                }

                if (question.getCorrectOption().equalsIgnoreCase(
                        submittedAnswer.selectedOption().trim())) {
                    score++;
                }
            }

            UserAccount student = userAccountRepository
                    .findByEmailIgnoreCase(authentication.getName())
                    .orElseThrow(() ->
                            new IllegalArgumentException(
                                    "Student account not found."));

            QuizTopic topic = topicRepository.findById(request.topicId())
                    .orElseThrow(() ->
                            new IllegalArgumentException("Topic not found."));

            QuizResult savedResult = quizResultService.saveResult(
                    student.getId(),
                    topic.getName(),
                    score,
                    questions.size()
            );

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(Map.of(
                            "message", "Quiz submitted successfully.",
                            "score", savedResult.getScore(),
                            "totalQuestions", savedResult.getTotalQuestions(),
                            "percentage", savedResult.getPercentage()
                    ));

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error",
                            e.getMessage() == null
                                    ? "Unable to submit quiz."
                                    : e.getMessage()));
        }
    }

    public record QuizSubmissionRequest(
            Long topicId,
            List<AnswerSubmission> answers) {
    }

    public record AnswerSubmission(
            Long questionId,
            String selectedOption) {
    }
}



