
package com.quizchamp;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface QuizCategoryRepository
        extends JpaRepository<QuizCategory, Long> {

    Optional<QuizCategory> findByNameIgnoreCase(String name);

    boolean existsByNameIgnoreCase(String name);
}