package org.tpc.form_builder.models.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.tpc.form_builder.models.AuditSnapShot;

public interface AuditSnapShotRepository extends JpaRepository<AuditSnapShot, Long> {
}
