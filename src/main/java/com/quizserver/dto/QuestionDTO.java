package com.quizserver.dto;

import com.quizserver.enums.QuestionType;
import lombok.Data;

@Data
public class QuestionDTO {

    private Long id;

    private String questionText;

    private String optionA;

    private String optionB;

    // null for TRUE_FALSE and SHORT_ANSWER questions
    private String optionC;

    // null for TRUE_FALSE and SHORT_ANSWER questions
    private String optionD;

    private String correctOption;

    private String explanation;

    private String imageName;

    private String imageContentType;

    private String imageUrl;

    private boolean hasImage;

    // The ID of the test this question belongs to
    private Long testId;

    // Defaults to MULTIPLE_CHOICE if not provided
    private QuestionType questionType = QuestionType.MULTIPLE_CHOICE;
}
