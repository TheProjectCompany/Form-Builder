package org.tpc.form_builder.models.repository;

import org.tpc.form_builder.models.Dropdown;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DropdownRepository extends MongoRepository<Dropdown, String> {
    List<Dropdown> findAllByClientIdAndIdInAndIsActive(String clientId, List<String> ids, boolean isActive);
}
