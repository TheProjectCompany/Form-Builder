package org.tpc.form_builder.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.tpc.form_builder.constants.CommonConstants;
import org.tpc.form_builder.exception.BadRequestException;
import org.tpc.form_builder.models.FormField;
import org.tpc.form_builder.models.Profile;
import org.tpc.form_builder.models.repository.FormFieldRepository;
import org.tpc.form_builder.models.repository.ProfileRepository;
import org.tpc.form_builder.service.FormFieldService;
import org.tpc.form_builder.service.dto.FormFieldDto;
import org.tpc.form_builder.service.mapper.FormFieldMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Log4j2
public class FormFieldServiceImpl implements FormFieldService {

    private final ProfileRepository profileRepository;
    private final FormFieldRepository formFieldRepository;
    private final FormFieldMapper formFieldMapper;

    @Override
    public FormField getFormFieldById(String id) {
        return null;
    }

    @Override
    public List<FormField> getFormFieldsBySectionId(String sectionId) {
        return List.of();
    }

    @Override
    public FormFieldDto createFormField(String profileId, String sectionId, FormFieldDto formFieldDto) {
        // 1. Fetch profile (to validate existence and status)
        Profile parentProfile = profileRepository.findByClientIdAndIdAndIsActive(
                CommonConstants.DEFAULT_CLIENT,
                formFieldDto.getProfileId(),
                Boolean.TRUE
        ).orElseThrow(() -> new BadRequestException("Parent profile not found"));

        // 2. Fetch all existing fields for that profile (used for validation)
        List<FormField> existingFields = formFieldRepository.findAllByClientIdAndProfileIdAndIsActive(
                CommonConstants.DEFAULT_CLIENT,
                formFieldDto.getProfileId(),
                Boolean.TRUE
        );

        // 3. Validate incoming field
        validateFormField(parentProfile, existingFields, formFieldDto);

        // 4. Save and return
        FormField formField = formFieldMapper.toEntity(formFieldDto);
        return formFieldMapper.toDto(formFieldRepository.save(formField));
    }

    private void validateFormField(Profile profile, List<FormField> existingFields, FormFieldDto formFieldDto) {
        validateSectionExists(profile, formFieldDto.getSectionId());
        validateKeywordUniqueness(existingFields, formFieldDto.getKeyword());
    }

    private void validateSectionExists(Profile profile, String sectionId) {
        boolean exists = profile.getSections().stream()
                .anyMatch(section -> section.getId().equals(sectionId));
        if (!exists) {
            throw new BadRequestException("No such section exists under the given profile");
        }
    }

    private void validateKeywordUniqueness(List<FormField> fields, String keyword) {
        if (fields != null && fields.stream().anyMatch(field -> keyword.equals(field.getKeyword()))) {
            throw new BadRequestException("Field keyword already exists");
        }
    }
}
