package org.tpc.form_builder.service.impl;

import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.tpc.form_builder.constants.CommonConstants;
import org.tpc.form_builder.exception.AlreadyExistsException;
import org.tpc.form_builder.exception.BadRequestException;
import org.tpc.form_builder.models.FormField;
import org.tpc.form_builder.models.Profile;
import org.tpc.form_builder.models.Section;
import org.tpc.form_builder.models.repository.FormFieldRepository;
import org.tpc.form_builder.models.repository.ProfileRepository;
import org.tpc.form_builder.service.ProfileService;
import org.tpc.form_builder.service.WatcherService;
import org.tpc.form_builder.service.dto.FormFieldDto;
import org.tpc.form_builder.service.dto.ProfileDto;
import org.tpc.form_builder.service.dto.SectionDto;
import org.tpc.form_builder.service.mapper.FormFieldMapper;
import org.tpc.form_builder.service.mapper.ProfileMapper;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Log4j2
public class ProfileServiceImpl implements ProfileService {

    private final ProfileRepository profileRepository;
    private final FormFieldRepository formFieldRepository;
    private final ProfileMapper profileMapper;
    private final FormFieldMapper formFieldMapper;
    private final WatcherService watcherService;

    @Override
    public ProfileDto createProfile(ProfileDto profileDto) {
        validateProfile(profileDto);
        Profile profile = profileMapper.toEntity(profileDto);
        return profileMapper.toDto(profileRepository.save(profile));
    }

    @Override
    @Transactional
    public ProfileDto createProfileSection(String profileId, SectionDto sectionDto) {
        Profile parentProfile = profileRepository.findByClientIdAndIdAndIsActive(CommonConstants.DEFAULT_CLIENT, profileId, Boolean.TRUE)
                .orElseThrow(() -> new BadRequestException("Parent profile not found"));
        validateSection(parentProfile, sectionDto);
        List<Section> existingSections = parentProfile.getSections();
        Section section = Section.builder()
                .id(UUID.randomUUID().toString())
                .name(sectionDto.getName())
                .sortOrder(existingSections.size() + 1)
                .build();
        parentProfile.getSections().add(section);
        return profileMapper.toDto(profileRepository.save(parentProfile));
    }

    @Override
    public FormFieldDto createProfileSectionField(@NotNull String profileId, @NotNull String sectionId, FormFieldDto formFieldDto) {
        Profile parentProfile = profileRepository.findByClientIdAndIdAndIsActive(CommonConstants.DEFAULT_CLIENT, profileId, Boolean.TRUE)
                .orElseThrow(() -> new BadRequestException("Parent profile not found"));
        parentProfile.getSections().stream()
                .filter(s -> s.getId().equals(sectionId))
                .findAny()
                .orElseThrow(() -> new BadRequestException("Section not found"));
        formFieldDto.setProfileId(profileId);
        formFieldDto.setSectionId(sectionId);
        FormField formField = formFieldMapper.toEntity(formFieldDto);
        formFieldRepository.save(formField);

        watcherService.registerWatchers(formField);

        return formFieldMapper.toDto(formField);
    }

    @Override
    public List<ProfileDto> getProfiles() {
        List<Profile> profilesList = profileRepository.findAll();
        if (!profilesList.isEmpty()) {
            return profileMapper.toDto(profilesList);
        }
        return List.of();
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
