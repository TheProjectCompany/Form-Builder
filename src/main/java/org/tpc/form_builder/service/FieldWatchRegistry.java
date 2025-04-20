package org.tpc.form_builder.service;

import org.tpc.form_builder.models.FormFieldData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FieldWatchRegistry {
    private final Map<String, List<FieldWatcher>> fieldWatchersMap =  new HashMap<>();

    public void registerFieldWatcher (String fieldId, FieldWatcher fieldWatcher) {
        fieldWatchersMap.computeIfAbsent(fieldId, k -> new ArrayList<>()).add(fieldWatcher);
    }

    public void notifyWatchers (String fieldId, List<String> newValues, Map<String, FormFieldData> allFieldData) {
        List<FieldWatcher> fieldWatchers = fieldWatchersMap.getOrDefault(fieldId, List.of());
        for (FieldWatcher fieldWatcher : fieldWatchers) {
            fieldWatcher.onFieldChanged(fieldId, newValues, allFieldData);
        }
    }
}
