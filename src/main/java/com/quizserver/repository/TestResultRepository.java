package com.quizserver.repository;

import com.quizserver.enteties.TestResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface TestResultRepository extends JpaRepository<TestResult, Long> {

    List<TestResult> findAllByUserId(Long userId);

    @Modifying
    @Transactional
    void deleteAllByTestId(Long testId);
}
