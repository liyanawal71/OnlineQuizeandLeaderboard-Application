package com.quizchamp;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "quiz_results")
public class QuizResult {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "student_id", nullable = false)
    private UserAccount student;

    @Column(nullable = false, length = 150)
    private String topicName;

    @Column(nullable = false)
    private int score;

    @Column(nullable = false)
    private int totalQuestions;

    @Column(nullable = false)
    private double percentage;

    @Column(nullable = false)
    private LocalDateTime completedAt;

    public QuizResult() {
    }

    @PrePersist
    public void beforeSave() {
        if (completedAt == null) {
            completedAt = LocalDateTime.now();
        }

        if (totalQuestions > 0) {
            percentage = (double) score / totalQuestions * 100;
        }
    }

    public Long getId() {
        return id;
    }

    public UserAccount getStudent() {
        return student;
    }

    public void setStudent(UserAccount student) {
        this.student = student;
    }

    public String getTopicName() {
        return topicName;
    }

    public void setTopicName(String topicName) {
        this.topicName = topicName;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public int getTotalQuestions() {
        return totalQuestions;
    }

    public void setTotalQuestions(int totalQuestions) {
        this.totalQuestions = totalQuestions;
    }

    public double getPercentage() {
        return percentage;
    }

    public LocalDateTime getCompletedAt() {
        return completedAt;
    }
}
