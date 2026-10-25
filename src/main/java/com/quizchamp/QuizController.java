
package com.quizchamp;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/admin")
public class QuizController {

    @GetMapping("/dashboard")
    public Map<String, Object> getDashboard() {
        return Map.of(
            "message", "Welcome to QuizChamp Admin Dashboard",
            "status", "success",
            "features", new String[] {
                "Manage Subjects",
                "Manage Topics",
                "Manage Questions",
                "View Leaderboard"
            }
        );
    }
}