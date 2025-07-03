package org.tpc.form_builder.service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.tpc.form_builder.models.Profile;
import org.tpc.form_builder.service.dto.ProfileDto;

@Mapper(componentModel = "spring")
public interface ProfileMapper extends EntityMapper<ProfileDto, Profile> {
    @Mapping(source = "id", target = "id")
    ProfileDto toDto(Profile profile);
    @Mapping(target = "clientId", expression = "java(org.tpc.form_builder.constants.CommonConstants.DEFAULT_CLIENT)")
    Profile toEntity(ProfileDto dto);
}
