package com.quizserver.service.test;

import com.quizserver.dto.*;
import com.quizserver.enteties.Question;
import java.util.List;
import org.springframework.web.multipart.MultipartFile;

public interface TestService {

    TestDTO createTest(TestDTO dto);

    QuestionDTO addQuestionInTest(QuestionDTO dto);

    QuestionDTO addQuestionInTestWithImage(QuestionDTO dto, MultipartFile image);

    List<TestDTO> getAllTest();

    TestDetailsDTO getAllQuestionsByTest(Long id);

    TestResultDTO submitTest(SubmitTestDTO request);

    List<TestResultDTO> getAllTestResults();

    List<TestResultDTO> getAllTestResultsOfUser(Long userId);

    void updateTest(Long id, TestDTO dto);

    void deleteTest(Long id);

    // ✅ Новые методы для ВОПРОСОВ
    QuestionDTO updateQuestion(Long id, QuestionDTO dto);

    QuestionDTO updateQuestionWithImage(Long id, QuestionDTO dto, MultipartFile image, boolean removeImage);

    void deleteQuestion(Long id);

    Question getQuestionImage(Long id);

    // ✅ Новый метод для получения детального разбора результата
    TestResultDTO getTestResultDetails(Long resultId);
}
