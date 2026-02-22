package com.quizserver.dto;

import lombok.Data;

/**
 * Used for user registration.
 */
@Data
public class SignupRequest {

    private String name;

    private Integer age;

    private String email;

    private String password;

    private String confirmPassword;
}
