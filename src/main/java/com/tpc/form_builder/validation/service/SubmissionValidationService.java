package com.tpc.form_builder.validation.service;

import com.tpc.form_builder.models.Form;
import com.tpc.form_builder.models.Submission;
import com.tpc.form_builder.validation.dto.ValidationError;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface SubmissionValidationService {
    Map<UUID, List<ValidationError>> validateSubmission(Form form, Submission submission);
}
