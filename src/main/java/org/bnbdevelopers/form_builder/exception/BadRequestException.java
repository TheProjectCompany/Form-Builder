package org.bnbdevelopers.form_builder.exception;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class BadRequestException extends RuntimeException {
    private final String value;

    public BadRequestException() {
        super();
        this.value = null;
    }

    public BadRequestException(String message) {
        super(message);
        this.value = null;
    }

    public BadRequestException(String message, String value) {
        super(message);
        this.value = value;
    }
}
