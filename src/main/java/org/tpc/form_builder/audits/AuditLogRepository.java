package org.tpc.form_builder.audits;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {

    Page<AuditLog> findAllByClientIdAndUser_PublicIdAndUser_IsActive(String clientId, String userPublicId, boolean isActive, Pageable pageable);

    Page<AuditLog> findAllByClientId(String clientId, Pageable pageable);

    Page<AuditLog> findAllByClientIdAndCompanyId(String clientId, String companyId, Pageable pageable);

    Page<AuditLog> findAllByClientIdAndInstanceId(String clientId, String instanceId, Pageable pageable);
}
