package com.tpc.form_builder.modules.formula.model;

public enum ResultType {
    STRING(String.class),
    BOOLEAN(Boolean.class),
    NUMBER(Double.class),
    INTEGER(Integer.class),
    DECIMAL(Double.class),
    LIST(java.util.List.class),
    DATE(java.time.LocalDate.class),
    DATETIME(java.time.LocalDateTime.class),
    OBJECT(Object.class);

    private final Class<?> javaType;

    ResultType(Class<?> javaType) { this.javaType = javaType; }

    public Class<?> getJavaType() { return javaType; }
}
