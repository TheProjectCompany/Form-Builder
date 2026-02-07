package com.tpc.form_builder.models;

import com.tpc.form_builder.models.enums.FieldType;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.UuidGenerator;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@EqualsAndHashCode(callSuper = true)
@Entity
@Table(indexes = {
        @Index(columnList = "submissionId"),
        @Index(columnList = "fieldId"),
        @Index(columnList = "tenantId")
})
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class FieldData extends BaseEntity{
    @Id
    @GeneratedValue
    @UuidGenerator(style = UuidGenerator.Style.TIME)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "submission_id", nullable = false)
    private Submission submission;

//    private UUID submissionId;
    private UUID fieldId;
    private FieldType fieldType;

    // Scalar Values for different field types
    private String textValue;
    private String longTextValue;
    private BigDecimal numberValue;
    private Boolean booleanValue;
    private LocalDate dateValue;
    private Instant dateTimeValue;

    @OneToMany(
            mappedBy = "fieldData",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.EAGER
    )
    @Builder.Default
    private List<FieldDataMultiValue> multiValues = new ArrayList<>();
}
