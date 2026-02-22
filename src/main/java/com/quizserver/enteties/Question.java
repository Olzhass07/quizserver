package com.quizserver.enteties;

import com.quizserver.dto.QuestionDTO;
import com.quizserver.enums.QuestionType;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class Question {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String questionText;

    // Used by MULTIPLE_CHOICE (A/B/C/D) and TRUE_FALSE (A=True, B=False)
    private String optionA;
    private String optionB;
    private String optionC; // null for TRUE_FALSE / SHORT_ANSWER
    private String optionD; // null for TRUE_FALSE / SHORT_ANSWER

    private String correctOption;

    private String explanation;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private QuestionType questionType = QuestionType.MULTIPLE_CHOICE;

    @ManyToOne
    @JoinColumn(name = "test_id")
    private Test test;

    public QuestionDTO getDto() {
        QuestionDTO dto = new QuestionDTO();
        dto.setId(id);
        dto.setQuestionText(questionText);
        dto.setOptionA(optionA);
        dto.setOptionB(optionB);
        dto.setOptionC(optionC);
        dto.setOptionD(optionD);
        dto.setCorrectOption(correctOption);
        dto.setExplanation(explanation);
        dto.setQuestionType(questionType);
        return dto;
    }
}
