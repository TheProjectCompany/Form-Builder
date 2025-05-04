package org.tpc.form_builder.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.tpc.form_builder.constants.CommonConstants;
import org.tpc.form_builder.enums.WatcherScope;
import org.tpc.form_builder.models.FieldWatcher;
import org.tpc.form_builder.models.FormField;
import org.tpc.form_builder.models.FormFieldData;
import org.tpc.form_builder.models.ProfileData;
import org.tpc.form_builder.models.repository.FieldWatcherRepository;
import org.tpc.form_builder.service.FieldWatchersFactory;
import org.tpc.form_builder.service.WatcherService;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Log4j2
public class WatcherServiceImpl implements WatcherService {

    private final FieldWatchersFactory fieldWatchersFactory;
    private final FieldWatcherRepository fieldWatcherRepository;

    @Override
    public void registerWatchers(FormField formField) {
        for (WatcherScope watcherScope : WatcherScope.values()) {
            fieldWatchersFactory.getFieldWatchers(watcherScope).registerWatcher(formField);
        }
    }

    @Override
    public void consumeWatchers(ProfileData instance, Map<String, FormFieldData> updatedFieldDataMap) {

        List<FieldWatcher> triggeredWatchers = fieldWatcherRepository.findAllByClientIdAndFieldIdIn(CommonConstants.DEFAULT_CLIENT, updatedFieldDataMap.keySet());

        for (WatcherScope watcherScope : WatcherScope.values()) {
            if (CollectionUtils.isEmpty(triggeredWatchers))
                return;
            List<FieldWatcher> currentScopeWatchers = triggeredWatchers.stream()
                    .filter(watcher -> watcher.getScope().equals(watcherScope))
                    .toList();
            fieldWatchersFactory.getFieldWatchers(watcherScope).consumeWatchers(currentScopeWatchers, instance);
        }
    }
}