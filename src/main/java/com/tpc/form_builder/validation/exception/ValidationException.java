package com.tpc.form_builder.validation.exception;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class ValidationException extends RuntimeException {
    private final String value;

    public ValidationException(){
        super();
        this.value = null;
    }

    public ValidationException(String message) {
        super(message);
        this.value = null;
    }

    public ValidationException(String message, String value){
        super(message);
        this.value = value;
    }
}
