package com.beginsecure.usersbchallenge.Persistence.Entity;

import java.util.Date;

import org.json.JSONObject;

import com.beginsecure.usersbchallenge.Util.Constants;
import com.beginsecure.usersbchallenge.Util.Functions;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Table;

@Entity
@Table(name="audit", schema=Constants.DB_NAME)
public class AuditEntity {
    @Column(name="id")
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Integer ID;

    @Column(name="process_uuid")
    private String processUUID;

    @Column(name="process")
    private String process;

    @Column(name="status")
    private String status;

    @Column(name="audit")
    private String audit;

    @Column(name="created_on")
    private String createdOn;

    public AuditEntity() {}

    public AuditEntity(AuditEntity audit) {
        this.process = audit.getProcess();
        this.processUUID = audit.getProcessUUID();
    }

    public AuditEntity(String UUID, String process) {
        this.processUUID = UUID;
        this.process = process;
    };

    public void begin(JSONObject audit){
        this.ID = null;
        this.status = Constants.AUDIT_STATUS_BEGIN;
        this.audit = audit == null ? null : audit.toString();
        this.createdOn = Functions.dateTimeToString(new Date(), Constants.DATE_TIME_MSEC_FORMAT);
    }

    public void exception(JSONObject audit){
        this.ID = null;
        this.status = Constants.AUDIT_STATUS_EXCEPTION;
        this.audit = audit == null ? null : audit.toString();
        this.createdOn = Functions.dateTimeToString(new Date(), Constants.DATE_TIME_MSEC_FORMAT);
    }

    public void end(JSONObject audit){
        this.ID = null;
        this.status = Constants.AUDIT_STATUS_END;
        this.audit = audit == null ? null : audit.toString();
        this.createdOn = Functions.dateTimeToString(new Date(), Constants.DATE_TIME_MSEC_FORMAT);
    }

    public String getProcessUUID() {
        return processUUID;
    }

    public String getProcess() {
        return process;
    }
    
}
