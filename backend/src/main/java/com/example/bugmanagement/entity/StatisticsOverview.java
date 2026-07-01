package com.example.bugmanagement.entity;

import java.util.Map;

public record StatisticsOverview(
        long total,
        long todayNew,
        long unresolved,
        long fixed,
        long closed,
        long serious,
        Map<String, Long> statusStats,
        Map<String, Long> severityStats
) {
}
