package com.quizserver.service.test;

import com.quizserver.dto.*;
import java.util.List;

public interface TestService {

    TestDTO createTest(TestDTO dto);

    QuestionDTO addQuestionInTest(QuestionDTO dto);

    List<TestDTO> getAllTest();

    TestDetailsDTO getAllQuestionsByTest(Long id);

    TestResultDTO submitTest(SubmitTestDTO request);

    List<TestResultDTO> getAllTestResults();

    List<TestResultDTO> getAllTestResultsOfUser(Long userId);

    // ✅ Новые методы
    void updateTest(Long id, TestDTO dto);

    void deleteTest(Long id);
}
