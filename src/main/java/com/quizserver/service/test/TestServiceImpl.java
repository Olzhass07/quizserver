package com.quizserver.service.test;

import com.quizserver.dto.*;
import com.quizserver.enteties.Question;
import com.quizserver.enteties.Test;
import com.quizserver.enteties.TestResult;
import com.quizserver.enteties.User;
import com.quizserver.repository.QuestionRepository;
import com.quizserver.repository.TestRepository;
import com.quizserver.repository.TestResultRepository;
import com.quizserver.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class TestServiceImpl implements TestService {

    @Autowired
    private TestRepository testRepository;

    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private TestResultRepository testResultRepository;

    @Autowired
    private UserRepository userRepository;

    @Override
    public TestDTO createTest(TestDTO dto) {
        Test test = new Test();
        test.setTitle(dto.getTitle());
        test.setDescription(dto.getDescription());
        test.setTime(dto.getTime());
        return testRepository.save(test).getDto();
    }

    @Override
    public QuestionDTO addQuestionInTest(QuestionDTO dto) {
        Test test = testRepository.findById(dto.getId())
                .orElseThrow(() -> new EntityNotFoundException("Test not found"));

        Question question = new Question();
        question.setTest(test);
        question.setQuestionText(dto.getQuestionText());
        question.setOptionA(dto.getOptionA());
        question.setOptionB(dto.getOptionB());
        question.setOptionC(dto.getOptionC());
        question.setOptionD(dto.getOptionD());
        question.setCorrectOption(dto.getCorrectOption());

        return questionRepository.save(question).getDto();
    }

    @Override
    public List<TestDTO> getAllTest() {
        return testRepository.findAll().stream()
                .map(test -> {
                    TestDTO dto = test.getDto();
                    int questionCount = (test.getQuestions() != null) ? test.getQuestions().size() : 0;
                    dto.setTime(questionCount * test.getTime());
                    return dto;
                })
                .toList();
    }


    @Override
    public TestDetailsDTO getAllQuestionsByTest(Long id) {
        Test test = testRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Test not found"));

        TestDetailsDTO dto = new TestDetailsDTO();
        TestDTO testDTO = test.getDto();
        dto.setTestDTO(testDTO);
        dto.setQuestions(test.getQuestions().stream().map(Question::getDto).toList());
        return dto;
    }

    @Override
    public TestResultDTO submitTest(SubmitTestDTO request) {
        Test test = testRepository.findById(request.getTestId())
                .orElseThrow(() -> new EntityNotFoundException("Test not found"));

        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        int correctAnswers = 0;
        for (QuestionResponse response : request.getResponses()) {
            Question question = questionRepository.findById(response.getQuestionId())
                    .orElseThrow(() -> new EntityNotFoundException("Question not found"));

            if (question.getCorrectOption().equals(response.getSelectedOption())) {
                correctAnswers++;
            }
        }

        int totalQuestions = test.getQuestions().size();
        double percentage = ((double) correctAnswers / totalQuestions) * 100;

        TestResult testResult = new TestResult();
        testResult.setTest(test);
        testResult.setUser(user);
        testResult.setTotalQuestions(totalQuestions);
        testResult.setCorrectAnswers(correctAnswers);
        testResult.setPercentage(percentage);

        return testResultRepository.save(testResult).getDto();
    }

    @Override
    public List<TestResultDTO> getAllTestResults() {
        return testResultRepository.findAll().stream().map(TestResult::getDto).toList();
    }

    @Override
    public List<TestResultDTO> getAllTestResultsOfUser(Long userId) {
        return testResultRepository.findAllByUserId(userId).stream().map(TestResult::getDto).toList();
    }

    // ✅ Новый метод: UPDATE
    @Override
    @Transactional
    public void updateTest(Long id, TestDTO dto) {
        Test test = testRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Test not found"));

        if (dto.getTitle() != null) test.setTitle(dto.getTitle());
        if (dto.getDescription() != null) test.setDescription(dto.getDescription());
        if (dto.getTime() != null) test.setTime(dto.getTime());

        testRepository.save(test);
    }

    // ✅ Новый метод: DELETE
    @Override
    @Transactional
    public void deleteTest(Long id) {
        if (!testRepository.existsById(id)) {
            throw new EntityNotFoundException("Test not found");
        }
        testResultRepository.deleteAllByTestId(id);
        questionRepository.deleteAllByTestId(id);
        testRepository.deleteById(id);
    }
}
