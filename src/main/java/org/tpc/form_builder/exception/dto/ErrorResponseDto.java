package org.tpc.form_builder.exception.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.http.HttpStatus;

import java.util.Date;
import java.util.List;
import java.util.Map;

@Data
public class ErrorResponseDto {

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy HH:mm:ss")
    private Date timestamp;

    private int code;
    private String status;
    private String message;
    private String path;
    private Map<String, List<String>> errors;
    private String stackTrace;

    public ErrorResponseDto() {
        timestamp = new Date();
    }

    public ErrorResponseDto(HttpStatus httpStatus, String message) {
        this();
        this.code = httpStatus.value();
        this.status = httpStatus.name();
        this.message = message;
    }

    public ErrorResponseDto(HttpStatus httpStatus, String message, Map<String, List<String>> errors) {
        this(httpStatus, message);
        this.errors = errors;
    }

    public ErrorResponseDto(HttpStatus httpStatus, String message, Map<String, List<String>> errors, String path) {
        this(httpStatus, message, errors);
        this.path = path;
    }

    public ErrorResponseDto(HttpStatus httpStatus, String message, String path, Map<String, List<String>> errors, String stackTrace) {
        this(httpStatus, message, errors, path);
        this.stackTrace = stackTrace;
    }
}