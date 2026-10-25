
package com.quizchamp;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/student/quizzes")
public class StudentQuizController {

    private final QuizQuestionRepository questionRepository;
    private final QuizTopicRepository topicRepository;

    public StudentQuizController(
            QuizQuestionRepository questionRepository,
            QuizTopicRepository topicRepository) {

        this.questionRepository = questionRepository;
        this.topicRepository = topicRepository;
    }

    // Get all topics created by the admin
    @GetMapping("/topics")
    public List<StudentTopicResponse> getTopics() {

        return topicRepository.findAll()
                .stream()
                .map(topic -> new StudentTopicResponse(
                        topic.getId(),
                        topic.getName(),
                        topic.getDescription()))
                .toList();
    }

    // Get questions belonging to a selected topic
    @GetMapping("/questions")
    public List<StudentQuestionResponse> getQuestions(
            @RequestParam Long topicId) {

        if (!topicRepository.existsById(topicId)) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Topic not found.");
        }

        return questionRepository.findByTopicId(topicId)
                .stream()
                .map(question -> new StudentQuestionResponse(
                        question.getId(),
                        question.getQuestionText(),
                        question.getOptionA(),
                        question.getOptionB(),
                        question.getOptionC(),
                        question.getOptionD()))
                .toList();
    }

    // Topic information returned to students
    public record StudentTopicResponse(
            Long id,
            String name,
            String description) {
    }

    // Question information returned to students.
    // The correct answer is deliberately excluded.
    public record StudentQuestionResponse(
            Long id,
            String questionText,
            String optionA,
            String optionB,
            String optionC,
            String optionD) {
    }
}

