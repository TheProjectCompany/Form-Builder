package org.tpc.form_builder.models.repository;

import org.tpc.form_builder.models.Profile;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProfileRepository extends MongoRepository<Profile, String> {
    boolean existsByClientIdAndName(String clientId, String name);

    Optional<Profile> findByClientIdAndIdAndIsActive(String clientId, String id, boolean isActive);
}
