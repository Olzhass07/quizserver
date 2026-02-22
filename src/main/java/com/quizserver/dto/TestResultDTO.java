package com.quizserver.dto;

import lombok.Data;
import java.util.List;

@Data
public class TestResultDTO {

    private Long id;

    private int totalQuestions;

    private int correctAnswers;

    private double percentage;

    private String testName;

    // Added so frontend can link back to the test
    private Long testId;

    private String userName;

    // Added so frontend can link to user profile
    private Long userId;

    /**
     * Per-question breakdown: populated when fetching a single result detail.
     * This will be null in list views (getAllTestResults) to keep responses lean.
     */
    private List<QuestionResultDetailDTO> questionDetails;
}
