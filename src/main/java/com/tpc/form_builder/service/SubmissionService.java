package com.tpc.form_builder.service;

import com.tpc.form_builder.models.Form;
import com.tpc.form_builder.models.Submission;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.util.UUID;

public interface SubmissionService {
    Submission submitForm(Submission submission);
    Submission submitForm(Form form, Submission submission);
    Submission getSubmissionById(UUID submissionId);
    Page<Submission> getSubmissionsByFormId(UUID formId, PageRequest pageRequest);
}
