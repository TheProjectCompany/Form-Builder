package org.tpc.form_builder.models.repository;

import org.springframework.data.mongodb.repository.Query;
import org.tpc.form_builder.enums.ComputationScope;
import org.tpc.form_builder.models.FormField;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

@Repository
public interface FormFieldRepository extends MongoRepository<FormField, String> {
    List<FormField> findAllByClientIdAndProfileIdAndIsActive(String clientId, String profileId, boolean isActive);

    List<FormField> findAllByClientIdAndIsActiveAndIdIn(String clientId, Boolean isActive, Collection<String> ids);

    @Query("""
{
  clientId: ?0,
  isActive: true,
  $or: [
    { computationRules: { $ne: null } },
    {
      $and: [
        { "computationRules.computationScope": { $ne: ?1 } },
        { "computationRules.dependsOn": { $in: ?2 } }
      ]
    }
  ]
}
""")
    List<FormField> findAllByClientIdAndIsActiveTrueAndComputationRules_ComputationScopeNotAndComputationRules_DependsOnIn(
            String clientId,
            ComputationScope computationScope,
            List<String> dependsOn
    );
}
