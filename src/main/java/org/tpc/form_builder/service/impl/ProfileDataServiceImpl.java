package org.tpc.form_builder.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.tpc.form_builder.constants.CommonConstants;
import org.tpc.form_builder.exception.BadRequestException;
import org.tpc.form_builder.models.FormFieldData;
import org.tpc.form_builder.utils.DataValidationUtility;
import org.tpc.form_builder.exception.DataValidationException;
import org.tpc.form_builder.models.ProfileData;
import org.tpc.form_builder.models.repository.ProfileDataRepository;
import org.tpc.form_builder.service.ProfileDataService;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Log4j2
public class ProfileDataServiceImpl  implements ProfileDataService {

    private final DataValidationUtility dataValidationUtility;
    private final ProfileDataRepository profileDataRepository;

    @Override
    public ProfileData createProfileData(ProfileData profileData) {
        Map<String, List<String>> validationErrors = dataValidationUtility.validateProfileData(profileData.getProfileId(), profileData.getDataMap());
        if (!validationErrors.isEmpty()) {
            throw new DataValidationException("Invalid Data", validationErrors);
        }
        return profileDataRepository.save(profileData);
    }

    @Override
    public Map<String, List<String>> validateProfileData(ProfileData profileData) {
        return dataValidationUtility.validateProfileData(profileData.getProfileId(), profileData.getDataMap());
    }

    @Override
    public ProfileData updateProfileData (String instanceId, ProfileData profileData) {
        ProfileData existingProfileData = profileDataRepository.findByClientIdAndId(CommonConstants.DEFAULT_CLIENT, instanceId)
                .orElseThrow(() -> new BadRequestException("Invalid Instance Id"));

        Map<String, List<String>> validationErrors = dataValidationUtility.validateProfileData(profileData.getProfileId(), profileData.getDataMap());
        if (!validationErrors.isEmpty()) {
            throw new DataValidationException("Invalid Data", validationErrors);
        }

        existingProfileData.setDataMap(profileData.getDataMap());
        return profileDataRepository.save(existingProfileData);
    }

    @Override
    public ProfileData updateProfileDataFields (String instanceId, Map<String, FormFieldData> fieldDataMap) {
        ProfileData profileData = profileDataRepository.findByClientIdAndId(CommonConstants.DEFAULT_CLIENT, instanceId)
                .orElseThrow(() -> new BadRequestException("Invalid Instance Id"));

        Map<String, List<String>> validationErrors = dataValidationUtility.validateSpecificFields(fieldDataMap);
        if (!validationErrors.isEmpty()) {
            throw new DataValidationException("Invalid Data", validationErrors);
        }

        if (profileData.getDataMap() == null)
            profileData.setDataMap(new HashMap<>());

        profileData.getDataMap().putAll(fieldDataMap);
        return profileDataRepository.save(profileData);
    }
}
