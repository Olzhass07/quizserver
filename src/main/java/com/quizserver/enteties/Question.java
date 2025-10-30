package com.quizserver.enteties;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class Question {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String questionText;


    private String optionA;

    private String optionB;

    private String optionC;

    private String optionD;


    private String correctOption;

    @ManyToOne
    @JoinColumn(name = "test_id")
    private Test test;
}
