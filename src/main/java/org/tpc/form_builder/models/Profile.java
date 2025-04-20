package org.tpc.form_builder.models;

import lombok.*;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Document(collection = "profile")
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class Profile extends BaseAttributes {
    @Id
    private String id;
    private String name;
    @Builder.Default
    private List<Section> sections = new ArrayList<>();
    private String clonedFrom;
    @Builder.Default
    private Integer sortOrder = 1;
    @Builder.Default
    private Boolean isActive = true;
}
