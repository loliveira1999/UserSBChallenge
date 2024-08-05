package com.beginsecure.usersbchallenge.Persistence.Service;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.beginsecure.usersbchallenge.Persistence.Entity.AuditEntity;
import com.beginsecure.usersbchallenge.Persistence.Repository.AuditRepository;

@Service
public class AuditService {

    private final AuditRepository auditRepository;

    @Autowired
    public AuditService(AuditRepository auditRepository) {
        this.auditRepository = auditRepository;
    }

    public void beginAudit(AuditEntity audit, JSONObject jsonAudit) {
        audit = new AuditEntity(audit);
        audit.begin(jsonAudit);
        auditRepository.save(audit);
    }

    public void exceptionAudit(AuditEntity audit, JSONObject jsonAudit) {
        audit = new AuditEntity(audit);
        audit.exception(jsonAudit);
        auditRepository.save(audit);
    }
    public void endAudit(AuditEntity audit, JSONObject jsonAudit) {
        audit = new AuditEntity(audit);
        audit.end(jsonAudit);
        auditRepository.saveAndFlush(audit);
    }
}
