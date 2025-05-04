package org.tpc.form_builder.models.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import org.tpc.form_builder.enums.WatcherScope;
import org.tpc.form_builder.models.FieldWatcher;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface FieldWatcherRepository extends MongoRepository<FieldWatcher, String> {

    List<FieldWatcher> findAllByClientIdAndFieldIdIn(String clientId, Collection<String> fieldIds);

    List<FieldWatcher> findAllByClientIdAndScopeAndFieldIdIn(String clientId, WatcherScope scope, Collection<String> fieldId);

    Optional<FieldWatcher> findByClientIdAndScopeAndFieldId(String clientId, WatcherScope scope, String fieldId);

    FieldWatcher findByClientIdAndScopeAndFieldIdIn(String clientId, WatcherScope scope, Collection<String> fieldIds);
}
