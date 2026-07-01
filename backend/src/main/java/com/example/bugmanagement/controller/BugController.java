package com.example.bugmanagement.controller;

import com.example.bugmanagement.common.ApiResponse;
import com.example.bugmanagement.common.UserContext;
import com.example.bugmanagement.dto.AssignRequest;
import com.example.bugmanagement.dto.BugRequest;
import com.example.bugmanagement.dto.StatusUpdateRequest;
import com.example.bugmanagement.entity.Bug;
import com.example.bugmanagement.entity.BugLog;
import com.example.bugmanagement.service.BugService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/bugs")
public class BugController {
    private final BugService bugService;

    public BugController(BugService bugService) {
        this.bugService = bugService;
    }

    @GetMapping
    public ApiResponse<List<Bug>> list(@RequestParam(required = false) String keyword,
                                       @RequestParam(required = false) String status,
                                       @RequestParam(required = false) String severity,
                                       @RequestParam(required = false) Long assigneeId) {
        return ApiResponse.success(bugService.findAll(UserContext.get(), keyword, status, severity, assigneeId));
    }

    @GetMapping("/{id}")
    public ApiResponse<Bug> detail(@PathVariable Long id) {
        return ApiResponse.success(bugService.findById(UserContext.get(), id));
    }

    @PostMapping
    public ApiResponse<Bug> create(@Valid @RequestBody BugRequest request) {
        return ApiResponse.success(bugService.create(UserContext.get(), request));
    }

    @PutMapping("/{id}")
    public ApiResponse<Bug> update(@PathVariable Long id, @Valid @RequestBody BugRequest request) {
        return ApiResponse.success(bugService.update(UserContext.get(), id, request));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        bugService.delete(UserContext.get(), id);
        return ApiResponse.success();
    }

    @PutMapping("/{id}/status")
    public ApiResponse<Bug> updateStatus(@PathVariable Long id, @Valid @RequestBody StatusUpdateRequest request) {
        return ApiResponse.success(bugService.updateStatus(UserContext.get(), id, request));
    }

    @PutMapping("/{id}/assign")
    public ApiResponse<Bug> assign(@PathVariable Long id, @Valid @RequestBody AssignRequest request) {
        return ApiResponse.success(bugService.assign(UserContext.get(), id, request));
    }

    @GetMapping("/{id}/logs")
    public ApiResponse<List<BugLog>> logs(@PathVariable Long id) {
        return ApiResponse.success(bugService.findLogs(UserContext.get(), id));
    }
}
