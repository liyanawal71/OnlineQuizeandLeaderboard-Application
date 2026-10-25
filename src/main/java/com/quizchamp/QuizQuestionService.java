
package com.quizchamp;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Locale;

@Service
@Transactional
public class QuizQuestionService {

    private final QuizQuestionRepository questionRepository;
    private final QuizTopicRepository topicRepository;

    public QuizQuestionService(
            QuizQuestionRepository questionRepository,
            QuizTopicRepository topicRepository) {
        this.questionRepository = questionRepository;
        this.topicRepository = topicRepository;
    }

    @Transactional(readOnly = true)
    public List<QuizQuestion> getAllQuestions() {
        return questionRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<QuizQuestion> getQuestionsByTopic(Long topicId) {
        if (!topicRepository.existsById(topicId)) {
            throw new IllegalArgumentException("Topic not found.");
        }

        return questionRepository.findByTopicId(topicId);
    }

    @Transactional(readOnly = true)
    public QuizQuestion getQuestionById(Long id) {
        return questionRepository.findById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException("Question not found."));
    }

    public QuizQuestion addQuestion(
            String questionText,
            String optionA,
            String optionB,
            String optionC,
            String optionD,
            String correctOption,
            Long topicId) {

        validateQuestion(
                questionText, optionA, optionB,
                optionC, optionD, correctOption);

        QuizTopic topic = topicRepository.findById(topicId)
                .orElseThrow(() ->
                        new IllegalArgumentException("Topic not found."));

        if (questionRepository
                .existsByQuestionTextIgnoreCaseAndTopicId(
                        questionText.trim(), topicId)) {
            throw new IllegalArgumentException(
                    "This question already exists in this topic.");
        }

        QuizQuestion question = new QuizQuestion(
                questionText.trim(),
                optionA.trim(),
                optionB.trim(),
                optionC.trim(),
                optionD.trim(),
                correctOption.trim().toUpperCase(Locale.ROOT),
                topic);

        return questionRepository.save(question);
    }

    public QuizQuestion updateQuestion(
            Long id,
            String questionText,
            String optionA,
            String optionB,
            String optionC,
            String optionD,
            String correctOption,
            Long topicId) {

        validateQuestion(
                questionText, optionA, optionB,
                optionC, optionD, correctOption);

        QuizQuestion question = getQuestionById(id);

        QuizTopic topic = topicRepository.findById(topicId)
                .orElseThrow(() ->
                        new IllegalArgumentException("Topic not found."));

        String normalizedText = questionText.trim();

        boolean duplicate = questionRepository
                .existsByQuestionTextIgnoreCaseAndTopicId(
                        normalizedText, topicId);

        if (duplicate
                && !(question.getTopic().getId().equals(topicId)
                && question.getQuestionText()
                        .equalsIgnoreCase(normalizedText))) {
            throw new IllegalArgumentException(
                    "This question already exists in this topic.");
        }

        question.setQuestionText(normalizedText);
        question.setOptionA(optionA.trim());
        question.setOptionB(optionB.trim());
        question.setOptionC(optionC.trim());
        question.setOptionD(optionD.trim());
        question.setCorrectOption(
                correctOption.trim().toUpperCase(Locale.ROOT));
        question.setTopic(topic);

        return questionRepository.save(question);
    }

    public void deleteQuestion(Long id) {
        QuizQuestion question = getQuestionById(id);
        questionRepository.delete(question);
    }

    private void validateQuestion(
            String questionText,
            String optionA,
            String optionB,
            String optionC,
            String optionD,
            String correctOption) {

        requireText(questionText, "Question");
        requireText(optionA, "Option A");
        requireText(optionB, "Option B");
        requireText(optionC, "Option C");
        requireText(optionD, "Option D");
        requireText(correctOption, "Correct option");

        if (questionText.trim().length() > 1000) {
            throw new IllegalArgumentException(
                    "Question must be 1000 characters or fewer.");
        }

        if (optionA.trim().length() > 500
                || optionB.trim().length() > 500
                || optionC.trim().length() > 500
                || optionD.trim().length() > 500) {
            throw new IllegalArgumentException(
                    "Each option must be 500 characters or fewer.");
        }

        String answer = correctOption.trim()
                .toUpperCase(Locale.ROOT);

        if (!List.of("A", "B", "C", "D").contains(answer)) {
            throw new IllegalArgumentException(
                    "Correct option must be A, B, C, or D.");
        }
    }

    private void requireText(String value, String field) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(
                    field + " cannot be empty.");
        }
    }
}
