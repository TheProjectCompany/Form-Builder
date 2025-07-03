package org.tpc.form_builder.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.tpc.form_builder.enums.WatcherScope;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class FieldWatchersFactory {

    private final Map<String, FieldWatchers> fieldWatchersMap;

    public FieldWatchers getFieldWatchers(WatcherScope watcherScope) {
        String beanKey = watcherScope.getBeanKey();
        FieldWatchers fieldWatchers = fieldWatchersMap.get(beanKey);

        if (fieldWatchers == null)
            throw new IllegalArgumentException("No watchers registered for scope: " + watcherScope);

        return fieldWatchers;
    }
}
