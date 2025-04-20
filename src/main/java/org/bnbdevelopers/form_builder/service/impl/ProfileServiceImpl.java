package org.bnbdevelopers.form_builder.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.bnbdevelopers.form_builder.constants.CommonConstants;
import org.bnbdevelopers.form_builder.exception.AlreadyExistsException;
import org.bnbdevelopers.form_builder.exception.BadRequestException;
import org.bnbdevelopers.form_builder.models.Profile;
import org.bnbdevelopers.form_builder.models.Section;
import org.bnbdevelopers.form_builder.models.repository.ProfileRepository;
import org.bnbdevelopers.form_builder.service.ProfileService;
import org.bnbdevelopers.form_builder.service.dto.ProfileDto;
import org.bnbdevelopers.form_builder.service.dto.SectionDto;
import org.bnbdevelopers.form_builder.service.mapper.ProfileMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Log4j2
public class ProfileServiceImpl implements ProfileService {

    private final ProfileRepository profileRepository;
    private final ProfileMapper profileMapper;

    @Override
    public ProfileDto createProfile(ProfileDto profileDto) {
        validateProfile(profileDto);
        Profile profile = profileMapper.toEntity(profileDto);
        return profileMapper.toDto(profileRepository.save(profile));
    }

    @Override
    @Transactional
    public ProfileDto createProfileSection(SectionDto sectionDto) {
        Profile parentProfile = profileRepository.findByClientIdAndIdAndIsActive(CommonConstants.DEFAULT_CLIENT, sectionDto.getParentProfileId(), Boolean.TRUE)
                .orElseThrow(() -> new BadRequestException("Parent profile not found"));
        validateSection(parentProfile, sectionDto);
        List<Section> existingSections = parentProfile.getSections();
        Section section = Section.builder()
                .name(sectionDto.getName())
                .sortOrder(existingSections.size() + 1)
                .build();
        parentProfile.getSections().add(section);
        return profileMapper.toDto(profileRepository.save(parentProfile));
    }

    private void validateProfile(ProfileDto profileDto) {
        if (profileRepository.existsByClientIdAndName(CommonConstants.DEFAULT_CLIENT, profileDto.getName())) {
            throw new AlreadyExistsException("Duplicate Profile Name");
        }
    }

    private void validateSection(Profile parentProfile, SectionDto sectionDto) {
        List<Section> existingSections = parentProfile.getSections();
        existingSections.stream().filter(section -> sectionDto.getName().equals(section.getName())).findAny().ifPresent(section -> {
            throw new AlreadyExistsException("Duplicate Section Name");
        });
    }
}
