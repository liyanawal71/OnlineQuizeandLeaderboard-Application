
package com.quizchamp;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class QuizResultService {

    private final QuizResultRepository quizResultRepository;
    private final UserAccountRepository userAccountRepository;

    public QuizResultService(
            QuizResultRepository quizResultRepository,
            UserAccountRepository userAccountRepository) {
        this.quizResultRepository = quizResultRepository;
        this.userAccountRepository = userAccountRepository;
    }

    @Transactional
    public QuizResult saveResult(
            Long studentId,
            String topicName,
            int score,
            int totalQuestions) {

        if (studentId == null) {
            throw new IllegalArgumentException(
                    "Student ID is required.");
        }

        if (topicName == null || topicName.isBlank()) {
            throw new IllegalArgumentException(
                    "Topic name is required.");
        }

        if (totalQuestions <= 0) {
            throw new IllegalArgumentException(
                    "Total questions must be greater than zero.");
        }

        if (score < 0 || score > totalQuestions) {
            throw new IllegalArgumentException(
                    "Score must be between zero and total questions.");
        }

        UserAccount student = userAccountRepository.findById(studentId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Student account not found."));

        if (!"ROLE_STUDENT".equalsIgnoreCase(student.getRole())
                && !"STUDENT".equalsIgnoreCase(student.getRole())) {
            throw new IllegalArgumentException(
                    "Only student accounts can save quiz results.");
        }

        QuizResult result = new QuizResult();
        result.setStudent(student);
        result.setTopicName(topicName.trim());
        result.setScore(score);
        result.setTotalQuestions(totalQuestions);

        return quizResultRepository.save(result);
    }
}

