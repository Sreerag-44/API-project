package com.example.demo.controller;

import com.example.demo.dto.SubmissionRequest;
import com.example.demo.entity.Submission;
import com.example.demo.repository.SubmissionRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Optional;

@RestController
@RequestMapping("/submissions")
public class SubmissionController {

    @Autowired
    private SubmissionRepository repository;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @PostMapping
    public ResponseEntity<?> createSubmission(@Valid @RequestBody SubmissionRequest req) {
        // Fast path: check if idempotency key already exists
        Optional<Submission> existing = repository.findByIdempotencyKey(req.getIdempotency_key());
        if (existing.isPresent()) {
            return ResponseEntity.ok(existing.get()); // 200 OK with existing record
        }

        try {
            String dataJson = objectMapper.writeValueAsString(req.getData());
            Submission sub = new Submission(
                req.getForm_id(),
                req.getIdempotency_key(),
                req.getEmail(),
                dataJson
            );

            Submission saved = repository.save(sub);
            return ResponseEntity.status(HttpStatus.CREATED).body(saved); // 201 Created

        } catch (DataIntegrityViolationException e) {
            // Concurrent race safety: two identical requests arrived simultaneously
            Submission conflictRecord = repository.findByIdempotencyKey(req.getIdempotency_key())
                .orElseThrow(() -> new RuntimeException("Database error"));
            return ResponseEntity.ok(conflictRecord);

        } catch (JsonProcessingException e) {
            return ResponseEntity.badRequest().body("Invalid data JSON structure");
        }
    }
}
