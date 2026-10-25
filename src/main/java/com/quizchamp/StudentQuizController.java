
package com.quizchamp;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/student")
public class StudentQuizController {

    private final QuizCategoryRepository categoryRepository;
    private final QuizTopicRepository topicRepository;
    private final QuizQuestionRepository questionRepository;

    public StudentQuizController(
            QuizCategoryRepository categoryRepository,
            QuizTopicRepository topicRepository,
            QuizQuestionRepository questionRepository) {

        this.categoryRepository = categoryRepository;
        this.topicRepository = topicRepository;
        this.questionRepository = questionRepository;
    }

    // STEP 1: Get all categories
    // URL: /api/student/categories
    @GetMapping("/categories")
    public List<CategoryResponse> getCategories() {

        return categoryRepository.findAll()
                .stream()
                .map(category -> new CategoryResponse(
                        category.getId(),
                        category.getName(),
                        category.getDescription()))
                .toList();
    }

    // STEP 2: Get topics inside a selected category
    // URL: /api/student/topics?categoryId=1
    @GetMapping("/topics")
    public List<TopicResponse> getTopics(
            @RequestParam Long categoryId) {

        if (!categoryRepository.existsById(categoryId)) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Category not found.");
        }

        return topicRepository.findByCategoryId(categoryId)
                .stream()
                .map(topic -> new TopicResponse(
                        topic.getId(),
                        topic.getName(),
                        topic.getDescription()))
                .toList();
    }

    // STEP 3: Get questions inside a selected topic
    // URL: /api/student/questions?topicId=1
    @GetMapping("/questions")
    public List<QuestionResponse> getQuestions(
            @RequestParam Long topicId) {

        if (!topicRepository.existsById(topicId)) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Topic not found.");
        }

        return questionRepository.findByTopicId(topicId)
                .stream()
                .map(question -> new QuestionResponse(
                        question.getId(),
                        question.getQuestionText(),
                        question.getOptionA(),
                        question.getOptionB(),
                        question.getOptionC(),
                        question.getOptionD()))
                .toList();
    }

    // Response data sent to the student website

    public record CategoryResponse(
            Long id,
            String name,
            String description) {
    }

    public record TopicResponse(
            Long id,
            String name,
            String description) {
    }

    public record QuestionResponse(
            Long id,
            String questionText,
            String optionA,
            String optionB,
            String optionC,
            String optionD) {
    }
}
