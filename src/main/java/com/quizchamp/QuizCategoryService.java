
package com.quizchamp;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class QuizCategoryService {

    private final QuizCategoryRepository repository;

    public QuizCategoryService(QuizCategoryRepository repository) {
        this.repository = repository;
    }

    public List<QuizCategory> getAllCategories() {
        return repository.findAll();
    }

    public QuizCategory getCategoryById(Long id) {
        return repository.findById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Category not found with ID: " + id
                        ));
    }

    @Transactional
    public QuizCategory addCategory(String name, String description) {
        String cleanName = validateName(name);

        if (repository.existsByNameIgnoreCase(cleanName)) {
            throw new IllegalArgumentException(
                    "A category with this name already exists."
            );
        }

        QuizCategory category =
                new QuizCategory(cleanName, cleanDescription(description));

        return repository.save(category);
    }

    @Transactional
    public QuizCategory updateCategory(
            Long id, String name, String description) {

        QuizCategory category = getCategoryById(id);
        String cleanName = validateName(name);

        repository.findByNameIgnoreCase(cleanName)
                .filter(existing -> !existing.getId().equals(id))
                .ifPresent(existing -> {
                    throw new IllegalArgumentException(
                            "A category with this name already exists."
                    );
                });

        category.setName(cleanName);
        category.setDescription(cleanDescription(description));

        return repository.save(category);
    }

    @Transactional
    public void deleteCategory(Long id) {
        QuizCategory category = getCategoryById(id);
        repository.delete(category);
    }

    private String validateName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException(
                    "Category name is required."
            );
        }

        String cleanName = name.trim();

        if (cleanName.length() > 100) {
            throw new IllegalArgumentException(
                    "Category name cannot exceed 100 characters."
            );
        }

        return cleanName;
    }

    private String cleanDescription(String description) {
        if (description == null || description.isBlank()) {
            return null;
        }

        String clean = description.trim();

        if (clean.length() > 500) {
            throw new IllegalArgumentException(
                    "Description cannot exceed 500 characters."
            );
        }

        return clean;
    }
}
