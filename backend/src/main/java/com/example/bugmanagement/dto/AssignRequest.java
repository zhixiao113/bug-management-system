package com.example.bugmanagement.dto;

import jakarta.validation.constraints.NotNull;

public class AssignRequest {
    @NotNull
    private Long assigneeId;
    private String remark;

    public Long getAssigneeId() {
        return assigneeId;
    }

    public void setAssigneeId(Long assigneeId) {
        this.assigneeId = assigneeId;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
}
