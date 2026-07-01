package com.example.bugmanagement.service;

import com.example.bugmanagement.common.AppException;
import com.example.bugmanagement.common.CurrentUser;
import com.example.bugmanagement.dto.AssignRequest;
import com.example.bugmanagement.dto.BugRequest;
import com.example.bugmanagement.dto.StatusUpdateRequest;
import com.example.bugmanagement.entity.Bug;
import com.example.bugmanagement.entity.BugLog;
import com.example.bugmanagement.repository.BugLogRepository;
import com.example.bugmanagement.repository.BugRepository;
import com.example.bugmanagement.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class BugService {
    private final BugRepository bugRepository;
    private final BugLogRepository bugLogRepository;
    private final UserRepository userRepository;

    public BugService(BugRepository bugRepository, BugLogRepository bugLogRepository, UserRepository userRepository) {
        this.bugRepository = bugRepository;
        this.bugLogRepository = bugLogRepository;
        this.userRepository = userRepository;
    }

    public List<Bug> findAll(CurrentUser currentUser, String keyword, String status, String severity, Long assigneeId) {
        Long queryCreatorId = currentUser.isTester() ? currentUser.id() : null;
        Long queryAssigneeId = currentUser.isDeveloper() ? currentUser.id() : assigneeId;
        return bugRepository.findAll(keyword, status, severity, queryAssigneeId, queryCreatorId);
    }

    public Bug findById(CurrentUser currentUser, Long id) {
        Bug bug = findExistingBug(id);
        assertCanView(currentUser, bug);
        return bug;
    }

    @Transactional
    public Bug create(CurrentUser currentUser, BugRequest request) {
        if (currentUser.isDeveloper()) {
            throw new AppException(403, "开发人员不能新增 Bug");
        }
        validateAssignee(request.getAssigneeId());
        Long bugId = bugRepository.create(request, currentUser.id());
        Bug bug = findExistingBug(bugId);
        bugLogRepository.create(bugId, currentUser.id(), "新增 Bug", null, "已提交",
                null, request.getAssigneeId(), "新增缺陷并指派处理人");
        return bug;
    }

    @Transactional
    public Bug update(CurrentUser currentUser, Long id, BugRequest request) {
        Bug oldBug = findExistingBug(id);
        assertCanEdit(currentUser, oldBug);
        validateAssignee(request.getAssigneeId());
        bugRepository.update(id, request);
        bugLogRepository.create(id, currentUser.id(), "编辑 Bug", oldBug.getStatus(), oldBug.getStatus(),
                oldBug.getAssigneeId(), request.getAssigneeId(), "编辑缺陷基础信息");
        return findExistingBug(id);
    }

    @Transactional
    public void delete(CurrentUser currentUser, Long id) {
        Bug oldBug = findExistingBug(id);
        if (!currentUser.isAdmin()) {
            throw new AppException(403, "只有管理员可以删除 Bug");
        }
        bugLogRepository.create(id, currentUser.id(), "删除 Bug", oldBug.getStatus(), oldBug.getStatus(),
                oldBug.getAssigneeId(), oldBug.getAssigneeId(), "管理员删除缺陷");
        bugRepository.delete(id);
    }

    @Transactional
    public Bug updateStatus(CurrentUser currentUser, Long id, StatusUpdateRequest request) {
        Bug oldBug = findExistingBug(id);
        assertCanChangeStatus(currentUser, oldBug, request.getStatus());
        bugRepository.updateStatus(id, request.getStatus());
        String operationType = "已关闭".equals(request.getStatus()) ? "关闭 Bug" : "修改状态";
        bugLogRepository.create(id, currentUser.id(), operationType, oldBug.getStatus(), request.getStatus(),
                oldBug.getAssigneeId(), oldBug.getAssigneeId(), request.getRemark());
        return findExistingBug(id);
    }

    @Transactional
    public Bug assign(CurrentUser currentUser, Long id, AssignRequest request) {
        Bug oldBug = findExistingBug(id);
        if (!(currentUser.isAdmin() || currentUser.isDeveloper())) {
            throw new AppException(403, "只有管理员和开发人员可以转交 Bug");
        }
        validateAssignee(request.getAssigneeId());
        bugRepository.updateAssignee(id, request.getAssigneeId());
        bugLogRepository.create(id, currentUser.id(), "转交处理人", oldBug.getStatus(), oldBug.getStatus(),
                oldBug.getAssigneeId(), request.getAssigneeId(), request.getRemark());
        return findExistingBug(id);
    }

    public List<BugLog> findLogs(CurrentUser currentUser, Long bugId) {
        Bug bug = findExistingBug(bugId);
        assertCanView(currentUser, bug);
        return bugLogRepository.findByBugId(bugId);
    }

    private Bug findExistingBug(Long id) {
        return bugRepository.findById(id)
                .orElseThrow(() -> new AppException(404, "Bug 不存在"));
    }

    private void validateAssignee(Long assigneeId) {
        if (assigneeId != null && userRepository.findById(assigneeId).isEmpty()) {
            throw new AppException(400, "处理人不存在");
        }
    }

    private void assertCanView(CurrentUser currentUser, Bug bug) {
        if (currentUser.isAdmin()) {
            return;
        }
        if (currentUser.isTester() && currentUser.id().equals(bug.getCreatorId())) {
            return;
        }
        if (currentUser.isDeveloper() && currentUser.id().equals(bug.getAssigneeId())) {
            return;
        }
        throw new AppException(403, "没有权限查看该 Bug");
    }

    private void assertCanEdit(CurrentUser currentUser, Bug bug) {
        if (currentUser.isAdmin()) {
            return;
        }
        if (currentUser.isTester() && currentUser.id().equals(bug.getCreatorId())) {
            return;
        }
        throw new AppException(403, "没有权限编辑该 Bug");
    }

    private void assertCanChangeStatus(CurrentUser currentUser, Bug bug, String targetStatus) {
        if (currentUser.isAdmin()) {
            return;
        }
        if (currentUser.isDeveloper() && currentUser.id().equals(bug.getAssigneeId()) && "已修复".equals(targetStatus)) {
            return;
        }
        if (currentUser.isTester()
                && currentUser.id().equals(bug.getCreatorId())
                && "已修复".equals(bug.getStatus())
                && "已关闭".equals(targetStatus)) {
            return;
        }
        throw new AppException(403, "当前角色不能执行该状态流转");
    }
}
