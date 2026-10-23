package com.example.demo.repository;

import com.example.demo.entity.Submission;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SubmissionRepository extends JpaRepository<Submission, Long> {
    Optional<Submission> findByIdempotencyKey(String idempotencyKey);

    // Fetch by form_id with pagination
    Page<Submission> findByFormId(String formId, Pageable pageable);
}
