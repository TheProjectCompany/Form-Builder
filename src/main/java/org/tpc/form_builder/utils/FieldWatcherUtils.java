package org.tpc.form_builder.utils;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;
import org.tpc.form_builder.constants.CommonConstants;
import org.tpc.form_builder.enums.WatcherScope;
import org.tpc.form_builder.models.FieldWatcher;
import org.tpc.form_builder.models.repository.FieldWatcherRepository;

import java.util.*;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Log4j2
public class FieldWatcherUtils {

    private final FieldWatcherRepository fieldWatcherRepository;

    @SuppressWarnings("unused")
    public void registerWatcher(WatcherScope watcherScope, String sourceFieldId, String profileId, String targetFieldId) {
        FieldWatcher newWatcher = org.tpc.form_builder.models.FieldWatcher.builder()
                .clientId(CommonConstants.DEFAULT_CLIENT)
                .fieldId(sourceFieldId)
                .affectingFields(Map.of(profileId, Set.of(targetFieldId)))
                .scope(watcherScope)
                .build();
        fieldWatcherRepository.save(newWatcher);
    }

    public void registerMultipleWatchers(WatcherScope watcherScope, Set<String> sourceFieldSet, String targetProfileId, String targetFieldId) {
        Map<String, FieldWatcher> registeredWatchers = fieldWatcherRepository.findAllByClientIdAndScopeAndFieldIdIn(CommonConstants.DEFAULT_CLIENT, watcherScope, sourceFieldSet)
                .stream()
                .collect(Collectors.toMap(
                        FieldWatcher::getFieldId,
                        fieldWatcher -> fieldWatcher
                ));

        List<FieldWatcher> updatedWatchers = new ArrayList<>();

        for (String sourceFieldId : sourceFieldSet) {
            if (registeredWatchers.containsKey(sourceFieldId)) {
                FieldWatcher watcher = registeredWatchers.get(sourceFieldId);
                addNewFieldToWatcher(watcher, targetProfileId, targetFieldId);
                updatedWatchers.add(watcher);
            }
            else {
                updatedWatchers.add(registerNewWatcher(watcherScope, sourceFieldId, targetProfileId, targetFieldId));
            }
        }

        fieldWatcherRepository.saveAll(updatedWatchers);
    }

    private void addNewFieldToWatcher(FieldWatcher watcher, String profileId, String fieldId) {
        if (watcher.getAffectingFields() == null)
            watcher.setAffectingFields(new HashMap<>());
        watcher.getAffectingFields().computeIfAbsent(profileId, k -> new HashSet<>()).add(fieldId);
    }

    private FieldWatcher registerNewWatcher(WatcherScope watcherScope, String sourceFieldId, String profileId, String fieldId) {
        return FieldWatcher.builder()
                .clientId(CommonConstants.DEFAULT_CLIENT)
                .fieldId(sourceFieldId)
                .affectingFields(Map.of(profileId, Set.of(fieldId)))
                .scope(watcherScope)
                .build();
    }
}
