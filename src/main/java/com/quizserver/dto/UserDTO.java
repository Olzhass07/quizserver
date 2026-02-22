package com.quizserver.dto;

import com.quizserver.enums.UserRole;
import lombok.Data;

@Data
public class UserDTO {
    private Long id;
    private String email;
    private String name;
    private Integer age;
    private UserRole role;

    // Statistics for profile
    private Integer testsTaken;
    private Double averageScore;

    // Actual history of tests
    private java.util.List<TestResultDTO> testResults;
}
