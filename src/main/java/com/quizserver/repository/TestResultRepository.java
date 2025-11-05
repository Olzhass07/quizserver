package com.quizserver.repository;

import com.quizserver.enteties.TestResult;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TestResultRepository extends JpaRepository<TestResult, Long> {

    List<TestResult> findAllByUserId(Long userId);

    void deleteAllByTestId(Long testId);
}

