package org.tpc.form_builder.service.dto;

import lombok.Data;

@Data
public class AuthRequestDto {
    private String username;
    private String password;
    private boolean rememberMe;
}
