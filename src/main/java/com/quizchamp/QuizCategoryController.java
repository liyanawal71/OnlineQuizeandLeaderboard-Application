
package com.quizchamp;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/categories")
public class QuizCategoryController {

    private final QuizCategoryService service;

    public QuizCategoryController(QuizCategoryService service) {
        this.service = service;
    }

    @GetMapping
    public List<QuizCategory> getAllCategories() {
        return service.getAllCategories();
    }

    @GetMapping("/{id}")
    public QuizCategory getCategory(@PathVariable Long id) {
        return service.getCategoryById(id);
    }

    @PostMapping
    public ResponseEntity<?> addCategory(
            @RequestBody Map<String, String> request) {
        try {
            QuizCategory category = service.addCategory(
                    request.get("name"),
                    request.get("description")
            );

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(category);

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateCategory(
            @PathVariable Long id,
            @RequestBody Map<String, String> request) {
        try {
            QuizCategory category = service.updateCategory(
                    id,
                    request.get("name"),
                    request.get("description")
            );

            return ResponseEntity.ok(category);

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteCategory(@PathVariable Long id) {
        try {
            service.deleteCategory(id);
            return ResponseEntity.noContent().build();

        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage()));
        }
    }
}
