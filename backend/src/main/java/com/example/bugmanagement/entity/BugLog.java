package com.example.bugmanagement.entity;

import java.time.LocalDateTime;

public class BugLog {
    private Long id;
    private Long bugId;
    private Long operatorId;
    private String operatorName;
    private String operationType;
    private String oldStatus;
    private String newStatus;
    private Long oldAssigneeId;
    private Long newAssigneeId;
    private String oldAssigneeName;
    private String newAssigneeName;
    private String remark;
    private LocalDateTime createdAt;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getBugId() {
        return bugId;
    }

    public void setBugId(Long bugId) {
        this.bugId = bugId;
    }

    public Long getOperatorId() {
        return operatorId;
    }

    public void setOperatorId(Long operatorId) {
        this.operatorId = operatorId;
    }

    public String getOperatorName() {
        return operatorName;
    }

    public void setOperatorName(String operatorName) {
        this.operatorName = operatorName;
    }

    public String getOperationType() {
        return operationType;
    }

    public void setOperationType(String operationType) {
        this.operationType = operationType;
    }

    public String getOldStatus() {
        return oldStatus;
    }

    public void setOldStatus(String oldStatus) {
        this.oldStatus = oldStatus;
    }

    public String getNewStatus() {
        return newStatus;
    }

    public void setNewStatus(String newStatus) {
        this.newStatus = newStatus;
    }

    public Long getOldAssigneeId() {
        return oldAssigneeId;
    }

    public void setOldAssigneeId(Long oldAssigneeId) {
        this.oldAssigneeId = oldAssigneeId;
    }

    public Long getNewAssigneeId() {
        return newAssigneeId;
    }

    public void setNewAssigneeId(Long newAssigneeId) {
        this.newAssigneeId = newAssigneeId;
    }

    public String getOldAssigneeName() {
        return oldAssigneeName;
    }

    public void setOldAssigneeName(String oldAssigneeName) {
        this.oldAssigneeName = oldAssigneeName;
    }

    public String getNewAssigneeName() {
        return newAssigneeName;
    }

    public void setNewAssigneeName(String newAssigneeName) {
        this.newAssigneeName = newAssigneeName;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
