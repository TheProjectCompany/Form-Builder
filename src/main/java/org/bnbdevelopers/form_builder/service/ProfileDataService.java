package org.bnbdevelopers.form_builder.service;

import org.bnbdevelopers.form_builder.models.ProfileData;

import java.util.List;
import java.util.Map;

public interface ProfileDataService {
    ProfileData createProfileData(ProfileData profileData);
    Map<String, List<String>> validateProfileData(ProfileData profileData);
}
