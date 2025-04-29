package org.tpc.form_builder.models.repository;

import org.tpc.form_builder.enums.ComputationScope;
import org.tpc.form_builder.models.FormField;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FormFieldRepository extends MongoRepository<FormField, String> {
    List<FormField> findAllByClientIdAndProfileIdAndIsActive(String clientId, String profileId, boolean isActive);

    List<FormField> findAllByClientIdAndIsActiveAndIdIn(String clientId, Boolean isActive, List<String> ids);

    List<FormField> findAllByClientIdAndIsActiveTrueAndComputationRules_ComputationScopeNotAndComputationRules_DependsOnContains(String clientId, ComputationScope computationScope, List<String> dependsOn);

}
