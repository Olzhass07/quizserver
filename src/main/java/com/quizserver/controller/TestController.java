package com.quizserver.controller;

import com.quizserver.dto.QuestionDTO;
import com.quizserver.dto.SubmitTestDTO;
import com.quizserver.dto.TestDTO;
import com.quizserver.enteties.Question;
import com.quizserver.enums.QuestionType;
import com.quizserver.service.test.TestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("api/test")
public class TestController {

    @Autowired
    private TestService testService;

    @PostMapping()
    public ResponseEntity<?> createTest(@RequestBody TestDTO dto) {
        try {
            return new ResponseEntity<>(testService.createTest(dto), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/question")
    public ResponseEntity<?> addQuestionInTest(@RequestBody QuestionDTO dto) {
        try {
            return new ResponseEntity<>(testService.addQuestionInTest(dto), HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping(value = "/question-with-image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> addQuestionInTestWithImage(
            @RequestParam Long testId,
            @RequestParam(required = false) String questionText,
            @RequestParam(required = false) String optionA,
            @RequestParam(required = false) String optionB,
            @RequestParam(required = false) String optionC,
            @RequestParam(required = false) String optionD,
            @RequestParam(required = false) String correctOption,
            @RequestParam(required = false) String explanation,
            @RequestParam(required = false) String questionType,
            @RequestPart(required = false) MultipartFile image) {
        try {
            QuestionDTO dto = buildQuestionDTO(testId, questionText, optionA, optionB, optionC, optionD, correctOption,
                    explanation, questionType);
            return new ResponseEntity<>(testService.addQuestionInTestWithImage(dto, image), HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping()
    public ResponseEntity<?> getAllTest() {
        try {
            return new ResponseEntity<>(testService.getAllTest(), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getAllQuestions(@PathVariable Long id) {
        try {
            return new ResponseEntity<>(testService.getAllQuestionsByTest(id), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/submit-test")
    public ResponseEntity<?> submitTest(@RequestBody SubmitTestDTO dto) {
        try {
            return new ResponseEntity<>(testService.submitTest(dto), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateTest(@PathVariable Long id, @RequestBody TestDTO dto) {
        try {
            testService.updateTest(id, dto);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteTest(@PathVariable Long id) {
        try {
            testService.deleteTest(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // ✅ НОВЫЕ ЭНДПОИНТЫ ДЛЯ ВОПРОСОВ

    @PutMapping("/question/{id}")
    public ResponseEntity<?> updateQuestion(@PathVariable Long id, @RequestBody QuestionDTO dto) {
        try {
            return new ResponseEntity<>(testService.updateQuestion(id, dto), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping(value = "/question/{id}/with-image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> updateQuestionWithImage(
            @PathVariable Long id,
            @RequestParam(required = false) Long testId,
            @RequestParam(required = false) String questionText,
            @RequestParam(required = false) String optionA,
            @RequestParam(required = false) String optionB,
            @RequestParam(required = false) String optionC,
            @RequestParam(required = false) String optionD,
            @RequestParam(required = false) String correctOption,
            @RequestParam(required = false) String explanation,
            @RequestParam(required = false) String questionType,
            @RequestParam(defaultValue = "false") boolean removeImage,
            @RequestPart(required = false) MultipartFile image) {
        try {
            QuestionDTO dto = buildQuestionDTO(testId, questionText, optionA, optionB, optionC, optionD, correctOption,
                    explanation, questionType);
            return new ResponseEntity<>(testService.updateQuestionWithImage(id, dto, image, removeImage), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping("/question/{id}")
    public ResponseEntity<?> deleteQuestion(@PathVariable Long id) {
        try {
            testService.deleteQuestion(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/question/{id}/image")
    public ResponseEntity<?> getQuestionImage(@PathVariable Long id) {
        try {
            Question question = testService.getQuestionImage(id);
            MediaType mediaType = MediaType.APPLICATION_OCTET_STREAM;
            if (question.getImageContentType() != null) {
                mediaType = MediaType.parseMediaType(question.getImageContentType());
            }

            return ResponseEntity.ok()
                    .contentType(mediaType)
                    .header(HttpHeaders.CACHE_CONTROL, "no-cache, no-store, must-revalidate")
                    .body(question.getImageData());
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    private QuestionDTO buildQuestionDTO(Long testId, String questionText, String optionA, String optionB, String optionC,
            String optionD, String correctOption, String explanation, String questionType) {
        QuestionDTO dto = new QuestionDTO();
        dto.setTestId(testId);
        dto.setQuestionText(questionText);
        dto.setOptionA(optionA);
        dto.setOptionB(optionB);
        dto.setOptionC(optionC);
        dto.setOptionD(optionD);
        dto.setCorrectOption(correctOption);
        dto.setExplanation(explanation);
        if (questionType != null && !questionType.isBlank()) {
            try {
                dto.setQuestionType(QuestionType.valueOf(questionType.toUpperCase()));
            } catch (IllegalArgumentException ex) {
                throw new IllegalArgumentException("Invalid questionType. Allowed: MULTIPLE_CHOICE, TRUE_FALSE, SHORT_ANSWER");
            }
        }
        return dto;
    }

}
