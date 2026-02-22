package com.quizserver.enteties;

import jakarta.persistence.*;
import lombok.Data;

/**
 * Stores the user's selected answer for each question in a quiz attempt.
 * This is the core data needed for Detailed Result Breakdown.
 */
@Entity
@Data
public class ResultAnswerRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "test_result_id", nullable = false)
    private TestResult testResult;

    @ManyToOne
    @JoinColumn(name = "question_id", nullable = false)
    private Question question;

    // The option the user selected (e.g. "A", "B", "C", "D", "True", "False", or
    // free text)
    private String selectedOption;

    // Cached at save time so we don't re-query the question for correctness
    private boolean correct;
}
