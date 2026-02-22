package com.quizserver.repository;

import com.quizserver.enteties.ResultAnswerRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ResultAnswerRecordRepository extends JpaRepository<ResultAnswerRecord, Long> {

    List<ResultAnswerRecord> findAllByTestResultId(Long testResultId);
}
