package com.tpc.form_builder.models;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.UuidGenerator;

import java.util.UUID;

@EqualsAndHashCode(callSuper = true)
@Entity
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class Tenant extends BaseEntity {
    @Id
    @GeneratedValue
    @UuidGenerator(style = UuidGenerator.Style.TIME)
    private UUID id;

    private String name;
}
