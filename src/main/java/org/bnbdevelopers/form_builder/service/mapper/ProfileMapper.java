package org.bnbdevelopers.form_builder.service.mapper;

import org.mapstruct.Mapper;

import org.bnbdevelopers.form_builder.models.Profile;
import org.bnbdevelopers.form_builder.service.dto.ProfileDto;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ProfileMapper extends EntityMapper<ProfileDto, Profile> {
    @Mapping(source = "id", target = "id")
    ProfileDto toDto(Profile profile);
    Profile toEntity(ProfileDto dto);
}
