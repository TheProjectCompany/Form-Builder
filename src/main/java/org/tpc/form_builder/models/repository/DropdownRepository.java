package org.tpc.form_builder.models.repository;

import org.tpc.form_builder.models.Dropdown;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DropdownRepository extends MongoRepository<Dropdown, String> {
}
