package org.bnbdevelopers.form_builder.exception;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;
import java.util.Map;

@EqualsAndHashCode(callSuper = true)
@Data
public class DataValidationException extends BadRequestException {
    private final Map<String, List<String>> errors;

    public DataValidationException() {
        super();
        this.errors = null;
    }

    public DataValidationException(String message) {
        super(message);
        this.errors = null;
    }

    public DataValidationException(String message, Map<String, List<String>> errors) {
        super(message);
        this.errors = errors;
    }
}
