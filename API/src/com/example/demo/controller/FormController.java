package com.example.demo.controller;

import com.example.demo.entity.Submission;
import com.example.demo.repository.SubmissionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
public class FormController {

    @Autowired
    private SubmissionRepository repository;

    @GetMapping("/forms/{form_id}/submissions")
    public ResponseEntity<Map<String, Object>> getFormSubmissions(
            @PathVariable("form_id") String formId,
            @RequestParam(name = "page", defaultValue = "0") int page) {

        // Enforce 10 per page, sorted by newest first
        int pageSize = 10;
        Pageable pageable = PageRequest.of(page, pageSize, Sort.by("createdAt").descending());

        Page<Submission> submissionsPage = repository.findByFormId(formId, pageable);

        // Build a structured paginated response
        Map<String, Object> response = new HashMap<>();
        response.put("form_id", formId);
        response.put("submissions", submissionsPage.getContent());
        response.put("current_page", submissionsPage.getNumber());
        response.put("total_items", submissionsPage.getTotalElements());
        response.put("total_pages", submissionsPage.getTotalPages());

        return ResponseEntity.ok(response);
    }
}
