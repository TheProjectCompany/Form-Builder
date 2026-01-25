package com.tpc.form_builder.models;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Version;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.UuidGenerator;

import java.util.UUID;

@Entity
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class Submission extends BaseEntity{
    @Id
    @GeneratedValue
    @UuidGenerator(style = UuidGenerator.Style.TIME)
    private UUID id;

    private UUID formId;
    private UUID userId;

    @Version
    private int version;
}
