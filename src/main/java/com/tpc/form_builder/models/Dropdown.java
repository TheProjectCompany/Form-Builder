package com.tpc.form_builder.models;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.UuidGenerator;

import java.util.UUID;

@Entity
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Dropdown extends BaseEntity {
    @Id
    @GeneratedValue
    @UuidGenerator(style = UuidGenerator.Style.TIME)
    private UUID id;

    private String name;
    private String description;

    @Enumerated(EnumType.STRING)
    private ConfigType type;
}
