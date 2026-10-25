 
package com.quizchamp;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/topics")
public class QuizTopicController {

    private final QuizTopicService service;

    public QuizTopicController(QuizTopicService service) {
        this.service = service;
    }

    @GetMapping
    public List<QuizTopic> getTopics(
            @RequestParam(required = false) Long categoryId) {

        if (categoryId != null) {
            return service.getTopicsByCategory(categoryId);
        }

        return service.getAllTopics();
    }

    @GetMapping("/{id}")
    public QuizTopic getTopic(@PathVariable Long id) {
        return service.getAllTopics()
                .stream()
                .filter(topic -> topic.getId().equals(id))
                .findFirst()
                .orElseThrow(() ->
                        new IllegalArgumentException("Topic not found."));
    }

    @PostMapping
    public ResponseEntity<?> addTopic(
            @RequestBody Map<String, Object> request) {
        try {
            String name = (String) request.get("name");
            String description = (String) request.get("description");
            Long categoryId = Long.valueOf(
                    request.get("categoryId").toString());

            QuizTopic topic = service.addTopic(
                    name, description, categoryId);

            return ResponseEntity.status(HttpStatus.CREATED).body(topic);

        } catch (IllegalArgumentException | NullPointerException e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error",
                            e.getMessage() == null
                                    ? "Invalid topic details."
                                    : e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateTopic(
            @PathVariable Long id,
            @RequestBody Map<String, Object> request) {
        try {
            String name = (String) request.get("name");
            String description = (String) request.get("description");
            Long categoryId = Long.valueOf(
                    request.get("categoryId").toString());

            QuizTopic topic = service.updateTopic(
                    id, name, description, categoryId);

            return ResponseEntity.ok(topic);

        } catch (IllegalArgumentException | NullPointerException e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error",
                            e.getMessage() == null
                                    ? "Invalid topic details."
                                    : e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteTopic(@PathVariable Long id) {
        try {
            service.deleteTopic(id);
            return ResponseEntity.noContent().build();

        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage()));
        }
    }
}

