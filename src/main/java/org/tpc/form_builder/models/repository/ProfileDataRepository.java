package org.tpc.form_builder.models.repository;

import org.tpc.form_builder.models.ProfileData;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProfileDataRepository extends MongoRepository<ProfileData, String> {
    Optional<ProfileData> findByClientIdAndId(String clientId, String id);
}
