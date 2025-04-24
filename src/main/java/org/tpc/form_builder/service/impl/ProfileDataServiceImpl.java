package org.tpc.form_builder.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.tpc.form_builder.utils.DataValidationUtility;
import org.tpc.form_builder.exception.DataValidationException;
import org.tpc.form_builder.models.ProfileData;
import org.tpc.form_builder.models.repository.ProfileDataRepository;
import org.tpc.form_builder.service.ProfileDataService;
import org.springframework.stereotype.Service;

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
        Map<String, List<String>> validationErrors = dataValidationUtility.validateDataFields(profileData.getDataMap());
        if (!validationErrors.isEmpty()) {
            throw new DataValidationException("Invalid Data", validationErrors);
        }
        return profileDataRepository.save(profileData);
    }

    @Override
    public Map<String, List<String>> validateProfileData(ProfileData profileData) {
        return dataValidationUtility.validateDataFields(profileData.getDataMap());
    }
}
