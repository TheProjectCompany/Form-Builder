package org.tpc.form_builder.models;

import jakarta.persistence.MappedSuperclass;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.tpc.form_builder.constants.CommonConstants;

@EqualsAndHashCode(callSuper = true)
@Data
@MappedSuperclass
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class BaseAttributes extends AuditAttributes{
    @Builder.Default
    private String clientId = CommonConstants.DEFAULT_CLIENT;
}
