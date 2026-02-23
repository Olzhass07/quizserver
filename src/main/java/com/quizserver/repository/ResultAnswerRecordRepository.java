package com.quizserver.repository;

import com.quizserver.enteties.ResultAnswerRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface ResultAnswerRecordRepository extends JpaRepository<ResultAnswerRecord, Long> {

    List<ResultAnswerRecord> findAllByTestResultId(Long testResultId);

    @Modifying
    @Transactional
    @Query("DELETE FROM ResultAnswerRecord r WHERE r.question.id = :questionId")
    void deleteAllByQuestionId(@Param("questionId") Long questionId);

    @Modifying
    @Transactional
    @Query("DELETE FROM ResultAnswerRecord r WHERE r.question.test.id = :testId")
    void deleteAllByQuestionTestId(@Param("testId") Long testId);
}
