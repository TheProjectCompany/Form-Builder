package com.tpc.form_builder.models;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.UuidGenerator;

import java.util.UUID;

@EqualsAndHashCode(callSuper = true)
@Entity
@Data
public class Form extends BaseEntity {
    @Id
    @GeneratedValue
    @UuidGenerator(style = UuidGenerator.Style.TIME)
    private UUID id;

    private String name;
    private int version;
}
