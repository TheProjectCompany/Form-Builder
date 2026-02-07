package com.tpc.form_builder.validation.service.impl;

import com.tpc.form_builder.models.Field;
import com.tpc.form_builder.models.FieldData;
import com.tpc.form_builder.models.Form;
import com.tpc.form_builder.models.Submission;
import com.tpc.form_builder.utils.validation.EntityUtils;
import com.tpc.form_builder.validation.dto.ValidationContext;
import com.tpc.form_builder.validation.dto.ValidationError;
import com.tpc.form_builder.validation.service.FieldDataValidationService;
import com.tpc.form_builder.validation.service.SubmissionValidationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
@Log4j2
public class SubmissionValidationServiceImpl implements SubmissionValidationService {

    private final FieldDataValidationService fieldDataValidationService;

    @Override
    public Map<UUID, List<ValidationError>> validateSubmission(Form form, Submission submission) {
        if (submission == null || form == null || Objects.equals(form.getId(), submission.getFormId())) {
            return Map.of();
        }
        List<Field> fields = form.getFields();

        List<FieldData> fieldsData = submission.getDataList();
        Map<UUID, FieldData> fieldDataByFieldIdMap = EntityUtils.mapByAttribute(fieldsData, FieldData::getFieldId, true, true);

        Map<UUID, List<ValidationError>> validationErrors = new HashMap<>();

        for (Field field : fields) {
            FieldData fieldData = fieldDataByFieldIdMap.getOrDefault(field.getId(), null);
            // TODO - handle missing field data case, if field is required then add validation error for missing field data
            // TODO - handle passing validation Context
            List<ValidationError> errors = fieldDataValidationService.validateFieldData(field, new ValidationContext(), fieldData);
            if (!errors.isEmpty()) {
                validationErrors.put(field.getId(), errors);
            }
        }

        return validationErrors;
    }
}
