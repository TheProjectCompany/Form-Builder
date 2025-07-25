package org.tpc.form_builder.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.tpc.form_builder.service.ProfileService;
import org.tpc.form_builder.service.dto.FormFieldDto;
import org.tpc.form_builder.service.dto.ProfileDto;
import org.tpc.form_builder.service.dto.SectionDto;

import java.util.List;

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

    @PostMapping("/{profile-id}/section")
    public ResponseEntity<ProfileDto> createSection(@PathVariable("profile-id") String profileId, @RequestBody @Valid SectionDto sectionDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(profileService.createProfileSection(profileId, sectionDto));
    }

    @PostMapping("{profile-id}/section/{section-id}/field")
    public ResponseEntity<FormFieldDto> createFormField(@PathVariable("profile-id") String profileId, @PathVariable("section-id") String sectionId, @RequestBody @Valid FormFieldDto formFieldDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(profileService.createProfileSectionField(profileId, sectionId, formFieldDto));
    }

    @GetMapping
    public ResponseEntity<List<ProfileDto>> getAllProfiles() {
        return ResponseEntity.status(HttpStatus.OK).body(profileService.getProfiles());
    }
}
