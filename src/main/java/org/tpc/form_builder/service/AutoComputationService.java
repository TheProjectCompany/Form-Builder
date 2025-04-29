package org.tpc.form_builder.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.tpc.form_builder.constants.CommonConstants;
import org.tpc.form_builder.enums.ComputationScope;
import org.tpc.form_builder.models.FormField;
import org.tpc.form_builder.models.FormFieldData;
import org.tpc.form_builder.models.repository.FormFieldRepository;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Log4j2
public class AutoComputationService {

    private final FormFieldRepository formFieldRepository;

    public void triggerAutoComputations(Map<String, FormFieldData> updatedFieldDataMap) {
        log.info("Triggering auto-computations");
        List<FormField> affectingFields = formFieldRepository.findAllByClientIdAndIsActiveTrueAndComputationRules_ComputationScopeNotAndComputationRules_DependsOnContains(
                CommonConstants.DEFAULT_CLIENT,
                ComputationScope.DISABLED,
                updatedFieldDataMap.keySet().stream().toList()
        );
    }

    public void consumeUpdates() {
        log.info("Consuming auto-computations");
    }
}
