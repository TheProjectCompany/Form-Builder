package org.tpc.form_builder.service;

import org.tpc.form_builder.models.FormFieldData;

import java.util.List;
import java.util.Map;

public interface FieldWatcher {
    void onFieldChanged(String fieldId, List<String> newValues, Map<String, FormFieldData> allFieldValues);
}
