package org.tpc.form_builder.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.tpc.form_builder.service.AuthenticationService;
import org.tpc.form_builder.service.dto.AuthRequestDto;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Log4j2
public class AuthenticationController {

    private final AuthenticationService authenticationService;

    @PostMapping("/login")
    public String generateToken (@RequestBody AuthRequestDto authRequestDto) {
        log.info("Received authentication request");
        return authenticationService.authenticate(authRequestDto);
    }

}

