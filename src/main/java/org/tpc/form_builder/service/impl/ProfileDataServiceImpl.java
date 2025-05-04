package org.tpc.form_builder.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.tpc.form_builder.constants.CommonConstants;
import org.tpc.form_builder.exception.BadRequestException;
import org.tpc.form_builder.exception.DataValidationException;
import org.tpc.form_builder.models.FormFieldData;
import org.tpc.form_builder.models.ProfileData;
import org.tpc.form_builder.models.repository.ProfileDataRepository;
import org.tpc.form_builder.service.ProfileDataService;
import org.tpc.form_builder.utils.DataValidationUtility;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Log4j2
public class ProfileDataServiceImpl implements ProfileDataService {

    private final DataValidationUtility dataValidationUtility;
    private final ProfileDataRepository profileDataRepository;

    @Override
    public Map<String, List<String>> validateProfileData(ProfileData profileData) {
        return dataValidationUtility.validateProfileData(profileData.getProfileId(), profileData.getDataMap());
    }

    @Override
    public ProfileData createProfileData(ProfileData profileData) {
        Map<String, List<String>> validationErrors = dataValidationUtility.validateProfileData(profileData.getProfileId(), profileData.getDataMap());
        if (!validationErrors.isEmpty()) {
            throw new DataValidationException("Invalid Data", validationErrors);
        }
        return profileDataRepository.save(profileData);
    }

    @Override
    public ProfileData getProfileDataInstance(String instanceId) {
        ProfileData instance = profileDataRepository.findByClientIdAndId(CommonConstants.DEFAULT_CLIENT, instanceId)
                .orElseThrow(() -> new BadRequestException("Invalid Instance Id"));
        if (!CollectionUtils.isEmpty(instance.getDataMap())) {
            removeInvisibleFields(instance.getDataMap());
        }
        return instance;
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
        profileDataRepository.save(existingProfileData);

        triggerFieldWatchers(existingProfileData, profileData.getDataMap());

        return profileData;
    }

    @Override
    @Transactional
    public ProfileData updateProfileDataFields (String instanceId, Map<String, FormFieldData> updatedFields) {
        ProfileData profileData = profileDataRepository.findByClientIdAndId(CommonConstants.DEFAULT_CLIENT, instanceId)
                .orElseThrow(() -> new BadRequestException("Invalid Instance Id"));

        Map<String, List<String>> validationErrors = dataValidationUtility.validateSpecificFields(updatedFields);
        if (!validationErrors.isEmpty()) {
            throw new DataValidationException("Invalid Data", validationErrors);
        }

        if (profileData.getDataMap() == null)
            profileData.setDataMap(new HashMap<>());

        profileData.getDataMap().putAll(updatedFields);
        profileDataRepository.save(profileData);

        triggerFieldWatchers(profileData, updatedFields);

        return profileData;
    }

    private void removeInvisibleFields(Map<String, FormFieldData> fieldDataMap) {
        Map<String, FormFieldData> visibleFields = new HashMap<>();
        fieldDataMap.forEach((k, v) -> {
            if (v.isVisible()) {
                visibleFields.put(k, v);
            }
        });
        fieldDataMap.clear();
        fieldDataMap.putAll(visibleFields);
    }

    private void triggerFieldWatchers(ProfileData instance, Map<String, FormFieldData> updatedFieldDataMap) {
        // Use this method to trigger field watchers
    }
}
