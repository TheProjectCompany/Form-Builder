package org.tpc.form_builder.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.stereotype.Service;
import org.tpc.form_builder.exception.BadRequestException;
import org.tpc.form_builder.security.JWTUtils;
import org.tpc.form_builder.service.dto.AuthRequestDto;

@Service
@RequiredArgsConstructor
@Log4j2
public class AuthenticationService {
    private final AuthenticationManager authenticationManager;
    private final JWTUtils jwtUtils;

    public String authenticate(AuthRequestDto authRequestDto) {
        try {
            authenticationManager.authenticate(new org.springframework.security.authentication.UsernamePasswordAuthenticationToken(authRequestDto.getUsername(), authRequestDto.getPassword()));
            return jwtUtils.generateToken(authRequestDto.getUsername());
        } catch (Exception e) {
            log.error("Authentication failed for user: {}", authRequestDto.getUsername(), e);
            throw new BadRequestException("Invalid username or password");
        }
    }
}
