
package com.quizchamp;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/student/categories")
public class StudentCategoryController {

    private final QuizCategoryService categoryService;

    public StudentCategoryController(
            QuizCategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping
    public List<QuizCategory> getCategories() {
        return categoryService.getAllCategories();
    }
}


