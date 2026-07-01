package com.example.bugmanagement.repository;

import com.example.bugmanagement.dto.BugRequest;
import com.example.bugmanagement.entity.Bug;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Repository
public class BugRepository {
    private final JdbcTemplate jdbcTemplate;

    private final RowMapper<Bug> rowMapper = new RowMapper<>() {
        @Override
        public Bug mapRow(ResultSet rs, int rowNum) throws SQLException {
            Bug bug = new Bug();
            bug.setId(rs.getLong("id"));
            bug.setTitle(rs.getString("title"));
            bug.setModuleName(rs.getString("module_name"));
            bug.setSeverity(rs.getString("severity"));
            bug.setDescription(rs.getString("description"));
            bug.setReproduceSteps(rs.getString("reproduce_steps"));
            bug.setScreenshotUrl(rs.getString("screenshot_url"));
            bug.setStatus(rs.getString("status"));
            bug.setAssigneeId(readNullableLong(rs, "assignee_id"));
            bug.setCreatorId(readNullableLong(rs, "creator_id"));
            bug.setAssigneeName(rs.getString("assignee_name"));
            bug.setCreatorName(rs.getString("creator_name"));
            bug.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
            bug.setUpdatedAt(rs.getTimestamp("updated_at").toLocalDateTime());
            return bug;
        }
    };

    public BugRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<Bug> findAll(String keyword, String status, String severity, Long assigneeId, Long creatorId) {
        StringBuilder sql = new StringBuilder("""
                SELECT b.*, creator.nickname AS creator_name, assignee.nickname AS assignee_name
                FROM bugs b
                LEFT JOIN users creator ON b.creator_id = creator.id
                LEFT JOIN users assignee ON b.assignee_id = assignee.id
                WHERE 1 = 1
                """);
        List<Object> args = new ArrayList<>();

        if (keyword != null && !keyword.isBlank()) {
            sql.append(" AND b.title LIKE ?");
            args.add("%" + keyword.trim() + "%");
        }
        if (status != null && !status.isBlank()) {
            sql.append(" AND b.status = ?");
            args.add(status);
        }
        if (severity != null && !severity.isBlank()) {
            sql.append(" AND b.severity = ?");
            args.add(severity);
        }
        if (assigneeId != null) {
            sql.append(" AND b.assignee_id = ?");
            args.add(assigneeId);
        }
        if (creatorId != null) {
            sql.append(" AND b.creator_id = ?");
            args.add(creatorId);
        }

        sql.append(" ORDER BY b.updated_at DESC, b.id DESC");
        return jdbcTemplate.query(sql.toString(), rowMapper, args.toArray());
    }

    public Optional<Bug> findById(Long id) {
        try {
            return Optional.ofNullable(jdbcTemplate.queryForObject("""
                    SELECT b.*, creator.nickname AS creator_name, assignee.nickname AS assignee_name
                    FROM bugs b
                    LEFT JOIN users creator ON b.creator_id = creator.id
                    LEFT JOIN users assignee ON b.assignee_id = assignee.id
                    WHERE b.id = ?
                    """, rowMapper, id));
        } catch (EmptyResultDataAccessException exception) {
            return Optional.empty();
        }
    }

    public Long create(BugRequest request, Long creatorId) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement("""
                    INSERT INTO bugs (
                      title, module_name, severity, description, reproduce_steps,
                      screenshot_url, status, assignee_id, creator_id, created_at, updated_at
                    ) VALUES (?, ?, ?, ?, ?, ?, '已提交', ?, ?, ?, ?)
                    """, Statement.RETURN_GENERATED_KEYS);
            LocalDateTime now = LocalDateTime.now();
            ps.setString(1, request.getTitle());
            ps.setString(2, request.getModuleName());
            ps.setString(3, defaultText(request.getSeverity(), "中"));
            ps.setString(4, request.getDescription());
            ps.setString(5, request.getReproduceSteps());
            ps.setString(6, request.getScreenshotUrl());
            setNullableLong(ps, 7, request.getAssigneeId());
            ps.setLong(8, creatorId);
            ps.setTimestamp(9, Timestamp.valueOf(now));
            ps.setTimestamp(10, Timestamp.valueOf(now));
            return ps;
        }, keyHolder);
        return Objects.requireNonNull(keyHolder.getKey()).longValue();
    }

    public void update(Long id, BugRequest request) {
        jdbcTemplate.update("""
                UPDATE bugs
                SET title = ?, module_name = ?, severity = ?, description = ?, reproduce_steps = ?,
                    screenshot_url = ?, assignee_id = ?, updated_at = ?
                WHERE id = ?
                """,
                request.getTitle(),
                request.getModuleName(),
                defaultText(request.getSeverity(), "中"),
                request.getDescription(),
                request.getReproduceSteps(),
                request.getScreenshotUrl(),
                request.getAssigneeId(),
                LocalDateTime.now(),
                id
        );
    }

    public void updateStatus(Long id, String status) {
        jdbcTemplate.update(
                "UPDATE bugs SET status = ?, updated_at = ? WHERE id = ?",
                status,
                LocalDateTime.now(),
                id
        );
    }

    public void updateAssignee(Long id, Long assigneeId) {
        jdbcTemplate.update(
                "UPDATE bugs SET assignee_id = ?, updated_at = ? WHERE id = ?",
                assigneeId,
                LocalDateTime.now(),
                id
        );
    }

    public void delete(Long id) {
        jdbcTemplate.update("DELETE FROM bugs WHERE id = ?", id);
    }

    private Long readNullableLong(ResultSet rs, String columnName) throws SQLException {
        long value = rs.getLong(columnName);
        return rs.wasNull() ? null : value;
    }

    private void setNullableLong(PreparedStatement ps, int index, Long value) throws SQLException {
        if (value == null) {
            ps.setObject(index, null);
        } else {
            ps.setLong(index, value);
        }
    }

    private String defaultText(String value, String defaultValue) {
        return value == null || value.isBlank() ? defaultValue : value;
    }
}
