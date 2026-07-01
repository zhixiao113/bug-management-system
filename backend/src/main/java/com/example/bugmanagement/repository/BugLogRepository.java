package com.example.bugmanagement.repository;

import com.example.bugmanagement.entity.BugLog;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public class BugLogRepository {
    private final JdbcTemplate jdbcTemplate;

    private final RowMapper<BugLog> rowMapper = new RowMapper<>() {
        @Override
        public BugLog mapRow(ResultSet rs, int rowNum) throws SQLException {
            BugLog log = new BugLog();
            log.setId(rs.getLong("id"));
            log.setBugId(rs.getLong("bug_id"));
            log.setOperatorId(readNullableLong(rs, "operator_id"));
            log.setOperatorName(rs.getString("operator_name"));
            log.setOperationType(rs.getString("operation_type"));
            log.setOldStatus(rs.getString("old_status"));
            log.setNewStatus(rs.getString("new_status"));
            log.setOldAssigneeId(readNullableLong(rs, "old_assignee_id"));
            log.setNewAssigneeId(readNullableLong(rs, "new_assignee_id"));
            log.setOldAssigneeName(rs.getString("old_assignee_name"));
            log.setNewAssigneeName(rs.getString("new_assignee_name"));
            log.setRemark(rs.getString("remark"));
            log.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
            return log;
        }
    };

    public BugLogRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void create(Long bugId, Long operatorId, String operationType, String oldStatus, String newStatus,
                       Long oldAssigneeId, Long newAssigneeId, String remark) {
        jdbcTemplate.update("""
                INSERT INTO bug_operation_logs (
                  bug_id, operator_id, operation_type, old_status, new_status,
                  old_assignee_id, new_assignee_id, remark, created_at
                ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
                """,
                bugId,
                operatorId,
                operationType,
                oldStatus,
                newStatus,
                oldAssigneeId,
                newAssigneeId,
                remark,
                LocalDateTime.now()
        );
    }

    public List<BugLog> findByBugId(Long bugId) {
        return jdbcTemplate.query("""
                SELECT l.*,
                       operator.nickname AS operator_name,
                       old_user.nickname AS old_assignee_name,
                       new_user.nickname AS new_assignee_name
                FROM bug_operation_logs l
                LEFT JOIN users operator ON l.operator_id = operator.id
                LEFT JOIN users old_user ON l.old_assignee_id = old_user.id
                LEFT JOIN users new_user ON l.new_assignee_id = new_user.id
                WHERE l.bug_id = ?
                ORDER BY l.created_at DESC, l.id DESC
                """, rowMapper, bugId);
    }

    public void deleteByBugId(Long bugId) {
        jdbcTemplate.update("DELETE FROM bug_operation_logs WHERE bug_id = ?", bugId);
    }

    private Long readNullableLong(ResultSet rs, String columnName) throws SQLException {
        long value = rs.getLong(columnName);
        return rs.wasNull() ? null : value;
    }
}
