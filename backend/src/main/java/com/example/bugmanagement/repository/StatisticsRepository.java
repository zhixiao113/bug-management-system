package com.example.bugmanagement.repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.LinkedHashMap;
import java.util.Map;

@Repository
public class StatisticsRepository {
    private final JdbcTemplate jdbcTemplate;

    public StatisticsRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public long countAll() {
        return count("SELECT COUNT(*) FROM bugs");
    }

    public long countTodayNew() {
        return count("SELECT COUNT(*) FROM bugs WHERE DATE(created_at) = CURDATE()");
    }

    public long countUnresolved() {
        return count("SELECT COUNT(*) FROM bugs WHERE status <> '已关闭'");
    }

    public long countByStatus(String status) {
        return count("SELECT COUNT(*) FROM bugs WHERE status = ?", status);
    }

    public long countSerious() {
        return count("SELECT COUNT(*) FROM bugs WHERE severity = '严重'");
    }

    public Map<String, Long> countGroupByStatus() {
        return countGroup("SELECT status AS label, COUNT(*) AS total FROM bugs GROUP BY status ORDER BY total DESC");
    }

    public Map<String, Long> countGroupBySeverity() {
        return countGroup("SELECT severity AS label, COUNT(*) AS total FROM bugs GROUP BY severity ORDER BY total DESC");
    }

    private long count(String sql, Object... args) {
        Long result = jdbcTemplate.queryForObject(sql, Long.class, args);
        return result == null ? 0L : result;
    }

    private Map<String, Long> countGroup(String sql) {
        Map<String, Long> result = new LinkedHashMap<>();
        jdbcTemplate.query(sql, rs -> {
            result.put(rs.getString("label"), rs.getLong("total"));
        });
        return result;
    }
}
