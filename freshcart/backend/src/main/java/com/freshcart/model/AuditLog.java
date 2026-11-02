package com.freshcart.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "audit_logs")
public class AuditLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "action", nullable = false)
    private String action;

    @Column(name = "performed_by", nullable = false)
    private String performedBy;

    @Column(name = "target_user")
    private String targetUser;

    @Column(name = "target_id")
    private String targetId;

    @Column(name = "details", length = 1000)
    private String details;

    @Column(name = "performed_at", nullable = false)
    private LocalDateTime performedAt = LocalDateTime.now();

    public AuditLog() {}

    public AuditLog(String action, String performedBy, String targetUser, String targetId, String details) {
        this.action = action;
        this.performedBy = performedBy;
        this.targetUser = targetUser;
        this.targetId = targetId;
        this.details = details;
        this.performedAt = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getAction() { return action; }
    public void setAction(String action) { this.action = action; }
    public String getPerformedBy() { return performedBy; }
    public void setPerformedBy(String performedBy) { this.performedBy = performedBy; }
    public String getTargetUser() { return targetUser; }
    public void setTargetUser(String targetUser) { this.targetUser = targetUser; }
    public String getTargetId() { return targetId; }
    public void setTargetId(String targetId) { this.targetId = targetId; }
    public String getDetails() { return details; }
    public void setDetails(String details) { this.details = details; }
    public LocalDateTime getPerformedAt() { return performedAt; }
    public void setPerformedAt(LocalDateTime performedAt) { this.performedAt = performedAt; }
}
