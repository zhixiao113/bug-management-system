package com.example.bugmanagement.service;

import com.example.bugmanagement.entity.StatisticsOverview;
import com.example.bugmanagement.repository.StatisticsRepository;
import org.springframework.stereotype.Service;

@Service
public class StatisticsService {
    private final StatisticsRepository statisticsRepository;

    public StatisticsService(StatisticsRepository statisticsRepository) {
        this.statisticsRepository = statisticsRepository;
    }

    public StatisticsOverview overview() {
        return new StatisticsOverview(
                statisticsRepository.countAll(),
                statisticsRepository.countTodayNew(),
                statisticsRepository.countUnresolved(),
                statisticsRepository.countByStatus("已修复"),
                statisticsRepository.countByStatus("已关闭"),
                statisticsRepository.countSerious(),
                statisticsRepository.countGroupByStatus(),
                statisticsRepository.countGroupBySeverity()
        );
    }
}
