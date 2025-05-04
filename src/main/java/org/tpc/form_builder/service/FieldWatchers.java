package org.tpc.form_builder.service;

import org.tpc.form_builder.models.FieldWatcher;
import org.tpc.form_builder.models.FormField;
import org.tpc.form_builder.models.ProfileData;

import java.util.List;

public interface FieldWatchers {
    void registerWatcher(FormField formField);
    void updateWatchers(FormField formField);
    void consumeWatchers(List<FieldWatcher> watchers, ProfileData instance);
}
