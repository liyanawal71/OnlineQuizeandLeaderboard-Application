
package com.quizchamp;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuizResultRepository
        extends JpaRepository<QuizResult, Long> {

    // Retrieve all quiz results, highest percentage first.
    List<QuizResult> findAllByOrderByPercentageDescCompletedAtAsc();

    // Retrieve results for a particular student.
    List<QuizResult> findByStudentIdOrderByCompletedAtDesc(Long studentId);

    // Retrieve results for a particular topic.
    List<QuizResult> findByTopicNameIgnoreCaseOrderByPercentageDesc(
            String topicName);
}


