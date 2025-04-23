package org.tpc.form_builder.service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.tpc.form_builder.audits.AuditDto;
import org.tpc.form_builder.audits.AuditLog;
import org.tpc.form_builder.models.User;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface AuditLogMapper extends EntityMapper<AuditDto, AuditLog>{

    @Mapping(target = "associatedUserId", source = "user.id")
    AuditDto toDto(AuditLog auditLog);

    @Mapping(target = "user", expression = "java(mapUser(dto.getAssociatedUserId()))")
    AuditLog toEntity(AuditDto dto);

    default User mapUser(Long userId) {
        if (userId == null) return null;
        User user = new User();
        user.setId(userId);
        return user;
    }
}
