package org.bnbdevelopers.form_builder.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.bnbdevelopers.form_builder.models.Profile;
import org.bnbdevelopers.form_builder.service.ProfileService;
import org.bnbdevelopers.form_builder.service.dto.ProfileDto;
import org.bnbdevelopers.form_builder.service.dto.SectionDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/profile")
@RequiredArgsConstructor
@Log4j2
public class ProfileController {

    private final ProfileService profileService;

    @PostMapping
    public ResponseEntity<ProfileDto> createProfile(@RequestBody @Valid ProfileDto profileDto) {
        log.info("Request to create profile received");
        return ResponseEntity.status(HttpStatus.CREATED).body(profileService.createProfile(profileDto));
    }

    @PostMapping("/section")
    public ResponseEntity<ProfileDto> createSection(@RequestBody @Valid SectionDto sectionDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(profileService.createProfileSection(sectionDto));
    }
}
