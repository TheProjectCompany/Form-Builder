package org.tpc.form_builder.models;

import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.HashMap;
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
}
