
package com.quizchamp;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/leaderboard")
public class LeaderboardController {

    private final QuizResultRepository quizResultRepository;

    public LeaderboardController(
            QuizResultRepository quizResultRepository) {
        this.quizResultRepository = quizResultRepository;
    }

    @GetMapping
    public List<LeaderboardEntry> getLeaderboard() {

        return quizResultRepository
                .findAllByOrderByPercentageDescCompletedAtAsc()
                .stream()
                .map(result -> new LeaderboardEntry(
                        result.getStudent().getFullName(),
                        result.getTopicName(),
                        result.getScore(),
                        result.getTotalQuestions(),
                        result.getPercentage(),
                        result.getCompletedAt()
                ))
                .toList();
    }

    public record LeaderboardEntry(
            String studentName,
            String topicName,
            int score,
            int totalQuestions,
            double percentage,
            java.time.LocalDateTime completedAt
    ) {
    }
}

