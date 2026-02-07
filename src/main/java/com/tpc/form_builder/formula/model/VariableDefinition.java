package com.tpc.form_builder.formula.model;

public class VariableDefinition {
    private final String name;
    private final Class<?> type;

    public VariableDefinition(String name, Class<?> type) {
        this.name = name; this.type = type;
    }
    public String getName() { return name; }
    public Class<?> getType() { return type; }
}

