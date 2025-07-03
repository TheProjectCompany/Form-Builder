package org.tpc.form_builder.service;

import org.tpc.form_builder.models.FormField;
import org.tpc.form_builder.models.FormFieldData;
import org.tpc.form_builder.models.ProfileData;

import java.util.Map;

public interface WatcherService {
    void registerWatchers(FormField formField);
    void consumeWatchers(ProfileData instance, Map<String, FormFieldData> updatedFieldDataMap);
}
