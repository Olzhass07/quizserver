package com.quizserver.service.test;

import com.quizserver.dto.*;
import com.quizserver.enteties.Question;
import com.quizserver.enteties.ResultAnswerRecord;
import com.quizserver.enteties.Test;
import com.quizserver.enteties.TestResult;
import com.quizserver.enteties.User;
import com.quizserver.enums.QuestionType;
import com.quizserver.repository.QuestionRepository;
import com.quizserver.repository.ResultAnswerRecordRepository;
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

    @Autowired
    private ResultAnswerRecordRepository resultAnswerRecordRepository;

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
        // Look for the test using testId instead of id
        Test test = testRepository.findById(dto.getTestId())
                .orElseThrow(() -> new EntityNotFoundException("Test not found"));

        Question question = new Question();
        question.setTest(test);
        question.setQuestionText(dto.getQuestionText());
        question.setOptionA(dto.getOptionA());
        question.setOptionB(dto.getOptionB());
        question.setOptionC(dto.getOptionC());
        question.setOptionD(dto.getOptionD());
        question.setCorrectOption(dto.getCorrectOption());
        question.setQuestionType(dto.getQuestionType() != null ? dto.getQuestionType() : QuestionType.MULTIPLE_CHOICE);

        return questionRepository.save(question).getDto();
    }

    @Override
    @Transactional
    public QuestionDTO updateQuestion(Long id, QuestionDTO dto) {
        Question question = questionRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Question not found"));

        if (dto.getQuestionText() != null)
            question.setQuestionText(dto.getQuestionText());
        if (dto.getOptionA() != null)
            question.setOptionA(dto.getOptionA());
        if (dto.getOptionB() != null)
            question.setOptionB(dto.getOptionB());
        if (dto.getOptionC() != null)
            question.setOptionC(dto.getOptionC());
        if (dto.getOptionD() != null)
            question.setOptionD(dto.getOptionD());
        if (dto.getCorrectOption() != null)
            question.setCorrectOption(dto.getCorrectOption());
        if (dto.getQuestionType() != null)
            question.setQuestionType(dto.getQuestionType());

        return questionRepository.save(question).getDto();
    }

    @Override
    @Transactional
    public void deleteQuestion(Long id) {
        if (!questionRepository.existsById(id)) {
            throw new EntityNotFoundException("Question not found");
        }
        resultAnswerRecordRepository.deleteAllByQuestionId(id);
        questionRepository.deleteById(id);
    }

    @Override
    public List<TestDTO> getAllTest() {
        return testRepository.findAll().stream()
                .map(test -> {
                    TestDTO dto = test.getDto();
                    // Simply return the time stored in the test (total test time)
                    dto.setTime(test.getTime() != null ? test.getTime() : 0L);
                    return dto;
                })
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
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
    @Transactional
    public TestResultDTO submitTest(SubmitTestDTO request) {
        Test test = testRepository.findById(request.getTestId())
                .orElseThrow(() -> new EntityNotFoundException("Test not found"));

        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        // 1. Create the Result object
        TestResult testResult = new TestResult();
        testResult.setTest(test);
        testResult.setUser(user);

        int correctAnswersCount = 0;
        int totalQuestions = test.getQuestions().size();

        // 2. Process answers and save records
        List<ResultAnswerRecord> answerRecords = new java.util.ArrayList<>();

        for (QuestionResponse response : request.getResponses()) {
            Question question = questionRepository.findById(response.getQuestionId())
                    .orElseThrow(() -> new EntityNotFoundException("Question not found"));

            boolean isCorrect = question.getCorrectOption().equalsIgnoreCase(response.getSelectedOption());
            if (isCorrect) {
                correctAnswersCount++;
            }

            ResultAnswerRecord record = new ResultAnswerRecord();
            record.setTestResult(testResult);
            record.setQuestion(question);
            record.setSelectedOption(response.getSelectedOption());
            record.setCorrect(isCorrect);
            answerRecords.add(record);
        }

        double percentage = totalQuestions > 0 ? ((double) correctAnswersCount / totalQuestions) * 100 : 0;

        testResult.setTotalQuestions(totalQuestions);
        testResult.setCorrectAnswers(correctAnswersCount);
        testResult.setPercentage(percentage);
        testResult.setAnswerRecords(answerRecords);

        return testResultRepository.save(testResult).getDto();
    }

    @Override
    @Transactional(readOnly = true)
    public TestResultDTO getTestResultDetails(Long resultId) {
        TestResult result = testResultRepository.findById(resultId)
                .orElseThrow(() -> new EntityNotFoundException("Result not found"));

        TestResultDTO dto = result.getDto();

        // Build the question breakdown
        List<QuestionResultDetailDTO> details = result.getAnswerRecords().stream()
                .map(record -> {
                    QuestionResultDetailDTO detail = new QuestionResultDetailDTO();
                    detail.setQuestionId(record.getQuestion().getId());
                    detail.setQuestionText(record.getQuestion().getQuestionText());
                    detail.setSelectedOption(record.getSelectedOption());
                    detail.setCorrectOption(record.getQuestion().getCorrectOption());
                    detail.setCorrect(record.isCorrect());
                    return detail;
                }).toList();

        dto.setQuestionDetails(details);
        return dto;
    }

    @Override
    @Transactional(readOnly = true)
    public List<TestResultDTO> getAllTestResults() {
        return testResultRepository.findAll().stream().map(TestResult::getDto).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<TestResultDTO> getAllTestResultsOfUser(Long userId) {
        return testResultRepository.findAllByUserId(userId).stream().map(TestResult::getDto).toList();
    }

    @Override
    @Transactional
    public void updateTest(Long id, TestDTO dto) {
        Test test = testRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Test not found"));

        if (dto.getTitle() != null)
            test.setTitle(dto.getTitle());
        if (dto.getDescription() != null)
            test.setDescription(dto.getDescription());
        if (dto.getTime() != null)
            test.setTime(dto.getTime());

        testRepository.save(test);
    }

    @Override
    @Transactional
    public void deleteTest(Long id) {
        if (!testRepository.existsById(id)) {
            throw new EntityNotFoundException("Test not found");
        }
        List<TestResult> testResults = testResultRepository.findAllByTestId(id);
        for (TestResult testResult : testResults) {
            resultAnswerRecordRepository.deleteAllByTestResultId(testResult.getId());
        }
        testResultRepository.deleteAllByTestId(id);
        questionRepository.deleteAllByTestId(id);
        testRepository.deleteById(id);
    }
}
