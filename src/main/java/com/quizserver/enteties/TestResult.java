package com.quizserver.enteties;

import com.quizserver.dto.TestResultDTO;
import jakarta.persistence.*;
import lombok.Data;
import java.util.List;

@Entity
@Data
public class TestResult {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private int totalQuestions;

    private int correctAnswers;

    private double percentage;

    @ManyToOne
    @JoinColumn(name = "test_id")
    private Test test;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @OneToMany(mappedBy = "testResult", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ResultAnswerRecord> answerRecords;

    public TestResultDTO getDto() {
        TestResultDTO dto = new TestResultDTO();
        dto.setId(id);
        dto.setTotalQuestions(totalQuestions);
        dto.setCorrectAnswers(correctAnswers);
        dto.setPercentage(percentage);
        dto.setTestName(test.getTitle());
        dto.setTestId(test.getId());
        dto.setUserName(user.getName());
        dto.setUserId(user.getId());
        // questionDetails deliberately NOT set here – only populated in the detail
        // endpoint
        return dto;
    }
}
