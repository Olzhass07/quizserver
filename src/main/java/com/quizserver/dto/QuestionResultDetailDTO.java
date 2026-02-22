package com.quizserver.dto;

import lombok.Data;

/**
 * Represents the per-question breakdown in a test result.
 * Sent to the frontend so users can see which questions they got wrong.
 */
@Data
public class QuestionResultDetailDTO {

    private Long questionId;

    private String questionText;

    // What the user selected
    private String selectedOption;

    // The actually correct answer
    private String correctOption;

    private String explanation;

    // true if selectedOption equals correctOption
    private boolean correct;
}
