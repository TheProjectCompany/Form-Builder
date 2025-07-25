package org.tpc.form_builder.models;

import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.util.CollectionUtils;
import org.tpc.form_builder.audits.AuditAction;
import org.tpc.form_builder.audits.AuditConstants;
import org.tpc.form_builder.audits.AuditEntity;
import org.tpc.form_builder.audits.ChangeDto;
import org.tpc.form_builder.exception.BadRequestException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


@EqualsAndHashCode(callSuper = true)
@Document(collection = "profileData")
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class ProfileData extends BaseAttributes{
    @Id
    private String id;
    @NotNull(message = "Profile ID must not be null")
    private String profileId;
    @Builder.Default
    private Map<String, FormFieldData> dataMap = new HashMap<>();

    @Version
    private Integer version;

    public static Map<String, ChangeDto> auditComparator(ProfileData previousObject, ProfileData currentObject) {
        Map<String, ChangeDto> diffMap = new HashMap<>();
        if (previousObject != null) {
            // Compare Changes
            if (Boolean.FALSE.equals(currentObject.getIsActive())) {
                diffMap.put(AuditConstants.ENTITY,
                        ChangeDto.builder()
                            .action(AuditAction.DELETE)
                            .auditEntity(AuditEntity.ENTITY)
                            .build()
                );
                return diffMap;
            }
            compareFieldObject(previousObject.getDataMap(), currentObject.getDataMap(), diffMap);
        }
        // Case of a newly created object
        else {
            if (Boolean.FALSE.equals(currentObject.getIsActive())) {
                throw new BadRequestException("Cannot create an inactive profile data instance");
            }
            diffMap.put(AuditConstants.ENTITY,
                    ChangeDto.builder()
                        .action(AuditAction.CREATE)
                        .auditEntity(AuditEntity.ENTITY)
                        .build()
            );
        }
        return diffMap;
    }

    private static void compareFieldObject(Map<String, FormFieldData> previousData, Map<String, FormFieldData> newData, Map<String, ChangeDto> diffMap) {
        if (!CollectionUtils.isEmpty(previousData) && !CollectionUtils.isEmpty(newData)) {
            // Compare existing fields
            for (Map.Entry<String, FormFieldData> entry : previousData.entrySet()) {
                String key = entry.getKey();
                if (newData.containsKey(key)) {
                    FormFieldData previousValue = entry.getValue();
                    FormFieldData newValue = newData.get(key);

                    AuditAction action = previousValue.compareEquals(newValue);
                    if (action != null) {
                        diffMap.put(key, ChangeDto.builder()
                                .action(action)
                                .auditEntity(AuditEntity.FIELD)
                                .fieldType(previousValue.getFieldType())
                                .previousValues(List.of(previousValue))
                                .newValues(List.of(newValue))
                                .build());
                    }
                }
            }

            // Newly Created Values
            for (Map.Entry<String, FormFieldData> entry : newData.entrySet()) {
                String key = entry.getKey();
                if (!previousData.containsKey(key)) {
                    FormFieldData newValue = entry.getValue();
                    diffMap.put(key, ChangeDto.builder()
                            .action(AuditAction.CREATE)
                            .fieldType(newValue.getFieldType())
                            .previousValues(null)
                            .newValues(List.of(newValue))
                            .build());
                }
            }
        }
    }
}
