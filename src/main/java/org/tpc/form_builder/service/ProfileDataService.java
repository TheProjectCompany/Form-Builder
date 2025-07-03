package org.tpc.form_builder.service;

import org.tpc.form_builder.models.FormFieldData;
import org.tpc.form_builder.models.ProfileData;

import java.util.List;
import java.util.Map;

public interface ProfileDataService {
    Map<String, List<String>> validateProfileData(ProfileData profileData);
    ProfileData createProfileData(ProfileData profileData);
    ProfileData getProfileDataInstance(String instanceId);
    ProfileData updateProfileData(String instanceId, ProfileData profileData);
    ProfileData updateProfileDataFields(String instanceId, Map<String, FormFieldData> fieldDataMap);
}
