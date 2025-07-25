package org.tpc.form_builder.service;

import org.tpc.form_builder.service.dto.FormFieldDto;
import org.tpc.form_builder.service.dto.ProfileDto;
import org.tpc.form_builder.service.dto.SectionDto;

import java.util.List;

public interface ProfileService {
    ProfileDto createProfile(ProfileDto profileDto);
    ProfileDto createProfileSection(String profileId, SectionDto sectionDto);
    FormFieldDto createProfileSectionField(String profileId, String sectionId, FormFieldDto formFieldDto);
    List<ProfileDto> getProfiles();
}