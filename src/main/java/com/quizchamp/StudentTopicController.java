package com.quizchamp;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/student/topics")
public class StudentTopicController {


private final QuizTopicService topicService;

public StudentTopicController(QuizTopicService topicService) {
    this.topicService = topicService;
}

@GetMapping
public List<TopicResponse> getTopics(
        @RequestParam Long categoryId) {

    return topicService.getTopicsByCategory(categoryId)
            .stream()
            .map(topic -> new TopicResponse(
                    topic.getId(),
                    topic.getName()
            ))
            .toList();
}

public record TopicResponse(
        Long id,
        String name
) {
}


}
