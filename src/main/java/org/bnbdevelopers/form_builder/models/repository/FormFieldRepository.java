package org.bnbdevelopers.form_builder.models.repository;

import org.bnbdevelopers.form_builder.models.FormField;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FormFieldRepository extends MongoRepository<FormField, String> {
    List<FormField> findAllByClientIdAndProfileIdAndIsActive(String clientId, String profileId, boolean isActive);

    List<FormField> findAllByClientIdAndIsActiveAndIdIn(String clientId, Boolean isActive, List<String> ids);
}
