package com.tpc.form_builder.controllers;

import com.tpc.form_builder.models.Submission;
import com.tpc.form_builder.service.SubmissionService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/submit-form")
@RequiredArgsConstructor
public class SubmissionController {

    private final SubmissionService submissionService;

    @PostMapping
    public ResponseEntity<Submission> submitForm(@RequestBody Submission submission) {
        Submission result = submissionService.submitForm(submission);
        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }

    @GetMapping("/{submissionId}")
    public ResponseEntity<Submission> getSubmission(@PathVariable UUID submissionId) {
        Submission submission = submissionService.getSubmissionById(submissionId);
        return ResponseEntity.ok(submission);
    }

    @GetMapping("/form/{formId}")
    public ResponseEntity<Page<Submission>> getSubmissionsByFormId(@PathVariable UUID formId,
                                                                   @RequestParam(defaultValue = "0") int page,
                                                                @RequestParam(defaultValue = "10") int size) {
        Page<Submission> submissionsPage = submissionService.getSubmissionsByFormId(formId, PageRequest.of(page, size));
        return ResponseEntity.status(HttpStatus.OK).body(submissionsPage);
    }
}
