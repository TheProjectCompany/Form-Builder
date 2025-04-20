package org.bnbdevelopers.form_builder.service;

import org.bnbdevelopers.form_builder.service.dto.ProfileDto;
import org.bnbdevelopers.form_builder.service.dto.SectionDto;

public interface ProfileService {
    ProfileDto createProfile(ProfileDto profileDto);
    ProfileDto createProfileSection(SectionDto sectionDto);
}