 
package com.quizchamp;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface QuizTopicRepository
        extends JpaRepository<QuizTopic, Long> {

    List<QuizTopic> findByCategoryId(Long categoryId);

    boolean existsByNameIgnoreCaseAndCategoryId(
            String name, Long categoryId);
}
