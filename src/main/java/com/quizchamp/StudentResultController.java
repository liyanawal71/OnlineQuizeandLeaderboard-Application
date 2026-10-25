
package com.quizchamp;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/student/results")
public class StudentResultController {

    private final QuizResultService quizResultService;
    private final UserAccountRepository userAccountRepository;

    public StudentResultController(
            QuizResultService quizResultService,
            UserAccountRepository userAccountRepository) {
        this.quizResultService = quizResultService;
        this.userAccountRepository = userAccountRepository;
    }

    @PostMapping
    public ResponseEntity<?> saveResult(
            @RequestBody ResultRequest request,
            Authentication authentication) {

        if (authentication == null
                || !authentication.isAuthenticated()
                || "anonymousUser".equals(authentication.getName())) {

            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Please log in first."));
        }

        if (request == null
                || request.topicName() == null
                || request.topicName().isBlank()
                || request.totalQuestions() <= 0
                || request.score() < 0
                || request.score() > request.totalQuestions()) {

            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Invalid quiz result."));
        }

        try {
            UserAccount student = userAccountRepository
                    .findByEmailIgnoreCase(authentication.getName())
                    .orElseThrow(() ->
                            new IllegalArgumentException(
                                    "Student account not found."));

            QuizResult result = quizResultService.saveResult(
                    student.getId(),
                    request.topicName(),
                    request.score(),
                    request.totalQuestions()
            );

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(Map.of(
                            "message", "Quiz result saved successfully.",
                            "score", result.getScore(),
                            "totalQuestions", result.getTotalQuestions(),
                            "percentage", result.getPercentage()
                    ));

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", e.getMessage()));
        }
    }

    public record ResultRequest(
            String topicName,
            int score,
            int totalQuestions
    ) {
    }
}



