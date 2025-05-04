package org.tpc.form_builder.models;

import lombok.*;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.tpc.form_builder.enums.WatcherScope;

import java.util.Map;
import java.util.Set;

@EqualsAndHashCode(callSuper = true)
@Document(collection = "profile")
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class FieldWatcher extends BaseAttributes{
    @Id
    private String id;
    private String fieldId;
    private WatcherScope scope;
    // ProfileId to Its Fields List
    private Map<String, Set<String>> affectingFields;
}
