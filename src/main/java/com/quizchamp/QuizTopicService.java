
package com.quizchamp;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class QuizTopicService {

    private final QuizTopicRepository topicRepository;
    private final QuizCategoryRepository categoryRepository;

    public QuizTopicService(
            QuizTopicRepository topicRepository,
            QuizCategoryRepository categoryRepository) {
        this.topicRepository = topicRepository;
        this.categoryRepository = categoryRepository;
    }

    public List<QuizTopic> getAllTopics() {
        return topicRepository.findAll();
    }

    public List<QuizTopic> getTopicsByCategory(Long categoryId) {
        if (!categoryRepository.existsById(categoryId)) {
            throw new IllegalArgumentException("Category not found.");
        }

        return topicRepository.findByCategoryId(categoryId);
    }

    @Transactional
    public QuizTopic addTopic(
            String name, String description, Long categoryId) {

        String cleanName = validateName(name);

        QuizCategory category = categoryRepository.findById(categoryId)
                .orElseThrow(() ->
                        new IllegalArgumentException("Category not found."));

        if (topicRepository.existsByNameIgnoreCaseAndCategoryId(
                cleanName, categoryId)) {
            throw new IllegalArgumentException(
                    "This topic already exists in that category.");
        }

        return topicRepository.save(
                new QuizTopic(cleanName, cleanDescription(description), category));
    }

    @Transactional
    public QuizTopic updateTopic(
            Long id, String name, String description, Long categoryId) {

        QuizTopic topic = topicRepository.findById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException("Topic not found."));

        String cleanName = validateName(name);

        QuizCategory category = categoryRepository.findById(categoryId)
                .orElseThrow(() ->
                        new IllegalArgumentException("Category not found."));

        boolean duplicate = topicRepository
                .findByCategoryId(categoryId)
                .stream()
                .anyMatch(existing ->
                        !existing.getId().equals(id)
                        && existing.getName().equalsIgnoreCase(cleanName));

        if (duplicate) {
            throw new IllegalArgumentException(
                    "This topic already exists in that category.");
        }

        topic.setName(cleanName);
        topic.setDescription(cleanDescription(description));
        topic.setCategory(category);

        return topicRepository.save(topic);
    }

    @Transactional
    public void deleteTopic(Long id) {
        QuizTopic topic = topicRepository.findById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException("Topic not found."));

        topicRepository.delete(topic);
    }

    private String validateName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Topic name is required.");
        }

        String cleanName = name.trim();

        if (cleanName.length() > 100) {
            throw new IllegalArgumentException(
                    "Topic name cannot exceed 100 characters.");
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
                    "Description cannot exceed 500 characters.");
        }

        return clean;
    }
}
