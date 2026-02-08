package com.tpc.form_builder.service.impl;

import com.tpc.form_builder.models.Form;
import com.tpc.form_builder.models.Submission;
import com.tpc.form_builder.modules.validation.dto.ValidationError;
import com.tpc.form_builder.modules.validation.exception.ValidationException;
import com.tpc.form_builder.modules.validation.service.SubmissionValidationService;
import com.tpc.form_builder.repository.SubmissionRepository;
import com.tpc.form_builder.service.FormService;
import com.tpc.form_builder.service.SubmissionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Log4j2
public class SubmissionServiceImpl implements SubmissionService {

    private final FormService formService;
    private final SubmissionValidationService submissionValidationService;
    private final SubmissionRepository submissionRepository;

    @Override
    public Submission submitForm(Submission submission) {
        Form form = formService.getFormById(submission.getFormId());
        return submitForm(form, submission);
    }

    @Override
    public Submission submitForm(Form form, Submission submission) {
        validateSubmission(form, submission);

        submission.getDataList().forEach(fieldData -> fieldData.setSubmission(submission));
        return submissionRepository.save(submission);
    }

    @Override
    public Submission getSubmissionById(UUID submissionId) {
        return submissionRepository.findById(submissionId)
                .orElseThrow(() -> new RuntimeException("Submission not found with ID: " + submissionId));
    }

    @Override
    public Page<Submission> getSubmissionsByFormId(UUID formId, PageRequest pageRequest) {
        return submissionRepository.findAllByFormId(formId, pageRequest);
    }

    private void validateSubmission(Form form, Submission submission) {
        Map<UUID, List<ValidationError>> errors = submissionValidationService.validateSubmission(form, submission);
        if (!errors.isEmpty()) {
            throw new ValidationException("Submission validation failed with " + errors.size() + " errors");
        }
    }
}
