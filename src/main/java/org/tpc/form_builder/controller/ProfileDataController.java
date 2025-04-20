package org.tpc.form_builder.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.tpc.form_builder.models.ProfileData;
import org.tpc.form_builder.service.ProfileDataService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
    public ResponseEntity<Map<String, List<String>>> validateProfile(@RequestBody ProfileData profileData) {
        Map<String, List<String>> validationErrors = profileDataService.validateProfileData(profileData);
        if(!validationErrors.isEmpty()) {
            return new ResponseEntity<>(validationErrors, HttpStatus.BAD_REQUEST);
        }
        return ResponseEntity.status(HttpStatus.OK).build();

    }
}
