
package com.quizchamp;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/questions")
public class QuizQuestionController {

    private final QuizQuestionService questionService;

    public QuizQuestionController(QuizQuestionService questionService) {
        this.questionService = questionService;
    }

    @GetMapping
    public List<QuizQuestion> getQuestions(
            @RequestParam(required = false) Long topicId) {
        if (topicId != null) {
            return questionService.getQuestionsByTopic(topicId);
        }
        return questionService.getAllQuestions();
    }

    @GetMapping("/{id}")
    public QuizQuestion getQuestion(@PathVariable Long id) {
        return questionService.getQuestionById(id);
    }

    @PostMapping
    public ResponseEntity<?> addQuestion(
            @RequestBody Map<String, Object> data) {
        try {
            QuizQuestion question = questionService.addQuestion(
                    getString(data, "questionText"),
                    getString(data, "optionA"),
                    getString(data, "optionB"),
                    getString(data, "optionC"),
                    getString(data, "optionD"),
                    getString(data, "correctOption"),
                    getLong(data, "topicId"));

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(question);
        } catch (IllegalArgumentException | NullPointerException ex) {
            return errorResponse(ex.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateQuestion(
            @PathVariable Long id,
            @RequestBody Map<String, Object> data) {
        try {
            QuizQuestion question = questionService.updateQuestion(
                    id,
                    getString(data, "questionText"),
                    getString(data, "optionA"),
                    getString(data, "optionB"),
                    getString(data, "optionC"),
                    getString(data, "optionD"),
                    getString(data, "correctOption"),
                    getLong(data, "topicId"));

            return ResponseEntity.ok(question);
        } catch (IllegalArgumentException | NullPointerException ex) {
            return errorResponse(ex.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteQuestion(@PathVariable Long id) {
        try {
            questionService.deleteQuestion(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException ex) {
            return errorResponse(ex.getMessage());
        }
    }

    private String getString(Map<String, Object> data, String key) {
        Object value = data.get(key);
        if (value == null) {
            throw new IllegalArgumentException(
                    key + " is required.");
        }
        return value.toString();
    }

    private Long getLong(Map<String, Object> data, String key) {
        Object value = data.get(key);
        if (value == null) {
            throw new IllegalArgumentException(
                    key + " is required.");
        }
        try {
            return Long.valueOf(value.toString());
        } catch (NumberFormatException ex) {
            throw new IllegalArgumentException(
                    key + " must be a valid number.");
        }
    }

    private ResponseEntity<Map<String, String>> errorResponse(
            String message) {
        Map<String, String> error = new LinkedHashMap<>();
        error.put("error", message == null
                ? "Invalid request." : message);
        return ResponseEntity.badRequest().body(error);
    }
}