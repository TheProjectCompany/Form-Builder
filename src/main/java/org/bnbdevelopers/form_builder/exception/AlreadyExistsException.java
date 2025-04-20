package org.bnbdevelopers.form_builder.exception;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class AlreadyExistsException extends RuntimeException {
    private final String value;

    public AlreadyExistsException() {
        super();
        this.value = null;
    }

    public AlreadyExistsException(String message) {
        super(message);
        this.value = null;
    }

    public AlreadyExistsException(String message, String value) {
        super(message);
        this.value = value;
    }
}
