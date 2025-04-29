package org.tpc.form_builder.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
//import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.tpc.form_builder.models.FormFieldData;
import org.tpc.form_builder.models.ProfileData;
import org.tpc.form_builder.service.ProfileDataService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/profile/data")
@RequiredArgsConstructor
@Log4j2
public class ProfileDataController {

    private final ProfileDataService profileDataService;

    // TODO - Change to dto
    @PostMapping("/validate")
    public ResponseEntity<Map<String, List<String>>> validateProfileData(@RequestBody ProfileData profileData) {
        Map<String, List<String>> validationErrors = profileDataService.validateProfileData(profileData);
        if(!validationErrors.isEmpty()) {
            return new ResponseEntity<>(validationErrors, HttpStatus.BAD_REQUEST);
        }
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @PostMapping
//    @PreAuthorize("@permissionEvaluator.hasProfilePermission(authentication, #profileData.getProfileId(), 'CREATE')")
    public ResponseEntity<ProfileData> createProfileData(@RequestBody ProfileData profileData) {
        return ResponseEntity.status(HttpStatus.CREATED).body(profileDataService.createProfileData(profileData));
    }

    @PutMapping("/instance/{instance-id}")
    public ResponseEntity<ProfileData> updateProfileData(@PathVariable("instance-id") String instanceId, @RequestBody ProfileData profileData) {
        return ResponseEntity.status(HttpStatus.OK).body(profileDataService.updateProfileData(instanceId, profileData));
    }

    @PatchMapping("/instance/{instance-id}")
    public ResponseEntity<ProfileData> updateProfileDataFields(@PathVariable("instance-id") String instanceId, @RequestBody Map<String, FormFieldData> fieldData) {
        return ResponseEntity.status(HttpStatus.OK).body(profileDataService.updateProfileDataFields(instanceId, fieldData));
    }

    @DeleteMapping("/instance/{instance-id}")
    public ResponseEntity<Void> deleteProfileData(@PathVariable("instance-id") String instanceId) {
        // TODO - Complete
        log.info("Received request to delete profile data with id: {}", instanceId);
        return ResponseEntity.status(HttpStatus.OK).build();
    }
}
