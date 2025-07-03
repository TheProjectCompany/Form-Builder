package org.tpc.form_builder.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import org.tpc.form_builder.enums.ConditionType;
import org.tpc.form_builder.enums.FieldType;
import org.tpc.form_builder.enums.OperatorType;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ConditionBuilder {
    private String profileId;
    private String fieldId;
    private FieldType fieldType;
    private ConditionType conditionType;
    private OperatorType operatorType;
    private List<String> values;
    private List<ConditionBuilder> joinExpressions;
}
