package org.tpc.form_builder.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.tpc.form_builder.constants.CommonConstants;
import org.tpc.form_builder.enums.ExpressionType;
import org.tpc.form_builder.enums.OperationType;
import org.tpc.form_builder.enums.WatcherScope;
import org.tpc.form_builder.models.*;
import org.tpc.form_builder.models.repository.FormFieldRepository;
import org.tpc.form_builder.models.repository.ProfileDataRepository;
import org.tpc.form_builder.service.FieldWatchers;
import org.tpc.form_builder.utils.EvaluatorUtility;
import org.tpc.form_builder.utils.FieldWatcherUtils;

import java.util.*;
import java.util.stream.Collectors;

@Service("visibilityWatchers")
@RequiredArgsConstructor
@Log4j2
public class VisibilityWatchers implements FieldWatchers {

    private final FieldWatcherUtils fieldWatcherUtils;
    private final FormFieldRepository formFieldRepository;
    private final EvaluatorUtility evaluatorUtility;
    private final ProfileDataRepository profileDataRepository;

    @Override
    @Async("asyncTaskExecutor")
    public void registerWatcher(FormField formField) {
        log.debug("Registering field watchers for profileId: {} fieldId: {}", formField.getProfileId(), formField.getId());
        Map<String, Set<String>> sourceField = aggregateSourceFields(formField.getVisibilityRules());

        Set<String> sourceFieldsSet = sourceField.values().stream()
                .flatMap(Collection::stream)
                .collect(Collectors.toSet());

        if (CollectionUtils.isEmpty(sourceFieldsSet)) {
            log.debug("No watchers registered for profileId: {} fieldId: {}", formField.getProfileId(), formField.getId());
            return;
        }

        fieldWatcherUtils.registerMultipleWatchers(WatcherScope.VISIBILITY_COMPUTATION, sourceFieldsSet, formField.getProfileId(), formField.getId());
    }

    @Override
    public void updateWatchers(FormField formField) {
        // TODO - Complete this to update watchers when visibility rules change
    }

    @Override
    public void consumeWatchers(List<FieldWatcher> watchers, ProfileData instance) {
        if (CollectionUtils.isEmpty(watchers)) {
            return;
        }

        // TODO - Extend this to calculate for other instances as well
        Set<String> currentInstanceFieldIds = watchers.stream()
                .map(FieldWatcher::getAffectingFields)
                .filter(affectingFields -> affectingFields.containsKey(instance.getProfileId()))
                .map(watcher -> watcher.get(instance.getProfileId()))
                .flatMap(Collection::stream)
                .collect(Collectors.toSet());

        List<FormField> affectingFormFields = formFieldRepository.findAllByClientIdAndIsActiveAndIdIn(
                CommonConstants.DEFAULT_CLIENT,
                Boolean.TRUE,
                currentInstanceFieldIds
        );

        // Re-Calculate all affecting fields
        for (FormField affectingFormField : affectingFormFields) {
            reCalculateAffectingField(instance, affectingFormField);
        }

        profileDataRepository.save(instance);
    }

    private void reCalculateAffectingField(ProfileData instance, FormField formField) {
        if (formField.getVisibilityRules() == null || !Boolean.TRUE.equals(formField.getVisibilityRules().getEnabled())) {
            return;
        }
        Visibility visibility = formField.getVisibilityRules();
        if (instance.getDataMap() == null || !instance.getDataMap().containsKey(formField.getId())) {
            return;
        }
        try {
            List<String> expressionResult = visibility.getExpression().evaluateExpression(instance.getDataMap());
            if (CollectionUtils.isEmpty(expressionResult)) return; // Skip if no result from expression
            instance.getDataMap().get(formField.getId()).setVisible(Boolean.parseBoolean(expressionResult.getFirst()));
        }
        catch (IllegalArgumentException | ArithmeticException e) {
            log.error("Error evaluating visibility expression for fieldId: {} in instanceId: {}", formField.getId(), instance.getId(), e);
            return; // Skip this field if evaluation fails
        }
    }

    private Map<String, Set<String>> aggregateSourceFields(Visibility visibility) {
        if (visibility == null || !Boolean.TRUE.equals(visibility.getEnabled())) {
            return Map.of(); // Return an empty immutable map
        }

        Map<String, Set<String>> sourceFields = new HashMap<>();
        extractSourceFields(visibility.getExpression(), sourceFields);
        return sourceFields;
    }

    private void extractSourceFields(Expression expression, Map<String, Set<String>> resultMap) {
         if (expression == null) {
             return; // No operands to process
         }
         if (ExpressionType.LEAF.equals(expression.getExpressionType()) && !OperationType.CONSTANT.equals(expression.getOperatorType())) {
             resultMap.computeIfAbsent("PROFILE", k -> new HashSet<>()).add(expression.getFieldId());
         }
         if (CollectionUtils.isEmpty(expression.getOperands())) {
             for (Expression operand : expression.getOperands()) {
                 extractSourceFields(operand, resultMap);
             }
         }
    }
}
