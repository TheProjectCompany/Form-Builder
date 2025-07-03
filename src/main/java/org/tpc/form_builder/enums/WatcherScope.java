package org.tpc.form_builder.enums;

public enum WatcherScope {
//    AUTO_COMPUTATION,
//    VALIDATION_RULES,
    VISIBILITY_COMPUTATION("visibilityWatchers");

    private final String beanKey;

    public String getBeanKey() {
        return beanKey;
    }

    WatcherScope(String beanKey) {
        this.beanKey = beanKey;
    }
}
