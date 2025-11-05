package com.quizserver.repository;

import com.quizserver.enteties.Question;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface QuestionRepository extends JpaRepository<Question, Long> {
    void deleteAllByTestId(Long testId);
}


