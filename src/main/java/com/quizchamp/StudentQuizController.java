package com.quizchamp;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/student/questions")
public class StudentQuizController {

    private final QuizQuestionService questionService;

    public StudentQuizController(QuizQuestionService questionService) {
        this.questionService = questionService;
    }

    @GetMapping
    public List<StudentQuestionResponse> getQuestions(
            @RequestParam Long topicId) {

        return questionService.getQuestionsByTopic(topicId)
                .stream()
                .map(question -> new StudentQuestionResponse(
                        question.getId(),
                        question.getQuestionText(),
                        question.getOptionA(),
                        question.getOptionB(),
                        question.getOptionC(),
                        question.getOptionD()
                ))
                .toList();
    }

    public record StudentQuestionResponse(
            Long id,
            String questionText,
            String optionA,
            String optionB,
            String optionC,
            String optionD
    ) {
    }
}