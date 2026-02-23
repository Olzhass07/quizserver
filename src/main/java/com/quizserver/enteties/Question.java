package com.quizserver.enteties;

import com.quizserver.dto.QuestionDTO;
import com.quizserver.enums.QuestionType;
import jakarta.persistence.*;
import lombok.Data;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

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

    private String imageName;

    private String imageContentType;

    @Lob
    @Column(columnDefinition = "LONGBLOB")
    private byte[] imageData;

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
        dto.setImageName(imageName);
        dto.setImageContentType(imageContentType);
        dto.setHasImage(imageData != null && imageData.length > 0);
        if (id != null) {
            String imagePath = "/api/test/question/" + id + "/image";
            try {
                dto.setImageUrl(ServletUriComponentsBuilder.fromCurrentContextPath().path(imagePath).toUriString());
            } catch (IllegalStateException ex) {
                dto.setImageUrl(imagePath);
            }
        }
        if (test != null) {
            dto.setTestId(test.getId());
        }
        return dto;
    }
}
