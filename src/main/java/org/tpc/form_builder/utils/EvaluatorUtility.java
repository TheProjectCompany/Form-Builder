package org.tpc.form_builder.utils;

import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.tpc.form_builder.enums.ComputationScope;
import org.tpc.form_builder.models.ConditionBuilder;
import org.tpc.form_builder.models.FormFieldData;
import org.tpc.form_builder.models.ProfileData;

import java.util.*;

@Component
@RequiredArgsConstructor
@Log4j2
public class EvaluatorUtility {
    public boolean evaluateCondition(ProfileData currentInstance, ComputationScope computationScope, ConditionBuilder conditionBuilder) {
        return switch (computationScope) {
            case INSTANCE -> evaluateInstanceCondition(currentInstance, conditionBuilder);
            case TRANSACTION -> {
                log.info("Computation scope is not supported yet");
                yield false;
            }
            case DISABLED -> {
                log.info("Computation scope is disabled");
                yield false;
            }
        };
    }

    public boolean evaluateInstanceCondition(@NotNull ProfileData currentInstance, ConditionBuilder condition) {
        return switch (condition.getConditionType()) {
            case JOIN -> evaluateJoinCondition(currentInstance, condition);
            case AND -> evaluateAndCondition(currentInstance, condition);
            case OR -> evaluateOrCondition(currentInstance, condition);
        };
    }

    private boolean evaluateAndCondition(ProfileData instance, ConditionBuilder condition) {
        return condition.getJoinExpressions().stream()
                .allMatch(child -> evaluateInstanceCondition(instance, child));
    }

    private boolean evaluateOrCondition(ProfileData instance, ConditionBuilder condition) {
        return condition.getJoinExpressions().stream()
                .anyMatch(child -> evaluateInstanceCondition(instance, child));
    }

    private boolean evaluateJoinCondition(ProfileData instance, ConditionBuilder condition) {
        return switch (condition.getFieldType()) {
            case DROPDOWN -> evaluateDropdownCondition(instance, condition);
            case BOOLEAN -> evaluateCheckboxCondition(instance, condition);
            default -> {
                log.info("Field type ({}) is not supported yet for join condition", condition.getFieldType());
                yield false;
            }
        };
    }

    private boolean evaluateDropdownCondition(ProfileData instance, ConditionBuilder condition) {
        return switch (condition.getOperatorType()) {
            case EQUALS -> evaluateEqualsCondition(instance, condition);
            case NOT_EQUALS -> !evaluateEqualsCondition(instance, condition);
            case CONTAINS -> evaluateContainsAnyCondition(instance, condition);
            case NOT_CONTAINS -> !evaluateContainsAnyCondition(instance, condition);
            case CONTAINS_ALL -> evaluateContainsAllCondition(instance, condition);
            case IS_BLANK -> evaluateIsBlankCondition(instance, condition);
            case IS_NOT_BLANK -> evaluateIsNotBlankCondition(instance, condition);
            default -> {
                log.info("Operator type ({}) is not supported yet for dropdown condition", condition.getOperatorType());
                yield false;
            }
        };
    }

    private boolean evaluateCheckboxCondition(ProfileData instance, ConditionBuilder condition) {
        return switch (condition.getOperatorType()) {
            case IS_TRUE -> evaluateIsTrueCondition(instance, condition);
            case IS_FALSE -> !evaluateIsTrueCondition(instance, condition);
            default -> {
                log.info("Operator type ({}) is not supported yet for checkbox condition", condition.getOperatorType());
                yield false;
            }
        };
    }

    private Optional<FormFieldData> getFieldData(ProfileData instance, String fieldId) {
        if (instance == null || fieldId == null) return Optional.empty();
        return Optional.ofNullable(instance.getDataMap().get(fieldId));
    }

    private boolean evaluateEqualsCondition(ProfileData instance, ConditionBuilder condition) {
        return getFieldData(instance, condition.getFieldId())
                .map(data -> areListsEqualIgnoreOrder(data.getValues(), condition.getValues()))
                .orElse(false);
    }

    private boolean evaluateContainsAnyCondition(ProfileData instance, ConditionBuilder condition) {
        return getFieldData(instance, condition.getFieldId())
                .map(data -> l1ContainsAnyOfL2(data.getValues(), condition.getValues()))
                .orElse(false);
    }

    private boolean evaluateContainsAllCondition(ProfileData instance, ConditionBuilder condition) {
        return getFieldData(instance, condition.getFieldId())
                .map(data -> l1ContainsAllOfL2(data.getValues(), condition.getValues()))
                .orElse(false);
    }

    private boolean evaluateIsBlankCondition(ProfileData instance, ConditionBuilder condition) {
        return getFieldData(instance, condition.getFieldId())
                .map(data -> CollectionUtils.isEmpty(data.getValues()))
                .orElse(true);
    }

    private boolean evaluateIsNotBlankCondition(ProfileData instance, ConditionBuilder condition) {
        return getFieldData(instance, condition.getFieldId())
                .map(data -> !CollectionUtils.isEmpty(data.getValues()))
                .orElse(false);
    }

    private boolean evaluateIsTrueCondition(ProfileData instance, ConditionBuilder condition) {
        return getFieldData(instance, condition.getFieldId())
                .map(data -> !CollectionUtils.isEmpty(data.getValues())
                        && "true".equalsIgnoreCase(data.getValues().getFirst()))
                .orElse(false);
    }


    public static boolean areListsEqualIgnoreOrder(List<String> list1, List<String> list2) {
        if (list1 == null || list2 == null) return false;
        if (list1.size() != list2.size()) return false;

        Map<String, Integer> countMap = new HashMap<>();

        for (String item : list1) {
            countMap.put(item, countMap.getOrDefault(item, 0) + 1);
        }

        for (String item : list2) {
            if (!countMap.containsKey(item)) {
                return false;
            }
            countMap.put(item, countMap.get(item) - 1);
            if (countMap.get(item) == 0) {
                countMap.remove(item);
            }
        }

        return countMap.isEmpty();
    }

    public static boolean l1ContainsAllOfL2(List<String> l1, List<String> l2) {
        if (l1 == null || l2 == null) return false;
        if (l1.size() < l2.size()) return false;

        Map<String, Integer> l1Counts = new HashMap<>();
        for (String item : l1) {
            l1Counts.put(item, l1Counts.getOrDefault(item, 0) + 1);
        }

        for (String item : l2) {
            if (!l1Counts.containsKey(item) || l1Counts.get(item) == 0) {
                return false;
            }
            l1Counts.put(item, l1Counts.get(item) - 1);
        }

        return true;
    }

    public static boolean l1ContainsAnyOfL2(List<String> l1, List<String> l2) {
        if (l1 == null || l2 == null) return false;

        Set<String> set1 = new HashSet<>(l1);
        for (String item : l2) {
            if (set1.contains(item)) {
                return true;
            }
        }
        return false;
    }
}
