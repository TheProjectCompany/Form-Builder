package org.tpc.form_builder.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.tpc.form_builder.audits.AuditDto;
import org.tpc.form_builder.constants.CommonConstants;
import org.tpc.form_builder.service.AuditLogsService;

@RestController
@RequestMapping("/api/v1/audit-logs")
@RequiredArgsConstructor
@Log4j2
public class AuditLogsController {

    private final AuditLogsService auditLogsService;

    @GetMapping("/client")
    public ResponseEntity<Page<AuditDto>> getClientAuditLogs(@PageableDefault(size = 20, sort = CommonConstants.UPDATED_ON, direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.status(HttpStatus.OK).body(auditLogsService.getClientAuditLogs(CommonConstants.DEFAULT_CLIENT, pageable));
    }

    @GetMapping("/company/{company-id}")
    public ResponseEntity<Page<AuditDto>> getCompanyAuditLogs(@PathVariable("company-id") String companyId, @PageableDefault(size = 20, sort = CommonConstants.UPDATED_ON, direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.status(HttpStatus.OK).body(auditLogsService.getCompanyAuditLogs(CommonConstants.DEFAULT_CLIENT, companyId, pageable));
    }

    @GetMapping("/user/{user-public-id}")
    public ResponseEntity<Page<AuditDto>> getUserAuditLogs(@PathVariable("user-public-id") String userPublicId, @PageableDefault(size = 20, sort = CommonConstants.UPDATED_ON, direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.status(HttpStatus.OK).body(auditLogsService.getUsersAuditLogs(CommonConstants.DEFAULT_CLIENT, userPublicId, pageable));
    }

    @GetMapping("/instance/{instance-id}")
    public ResponseEntity<Page<AuditDto>> getInstanceAuditLogs(@PathVariable("instance-id") String instanceId, @PageableDefault(size = 20, sort = CommonConstants.UPDATED_ON, direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.status(HttpStatus.OK).body(auditLogsService.getInstanceAuditLogs(CommonConstants.DEFAULT_CLIENT, instanceId, pageable));
    }
}
