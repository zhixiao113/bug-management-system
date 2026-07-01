package com.example.bugmanagement.controller;

import com.example.bugmanagement.common.ApiResponse;
import com.example.bugmanagement.entity.StatisticsOverview;
import com.example.bugmanagement.service.StatisticsService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/statistics")
public class StatisticsController {
    private final StatisticsService statisticsService;

    public StatisticsController(StatisticsService statisticsService) {
        this.statisticsService = statisticsService;
    }

    @GetMapping("/overview")
    public ApiResponse<StatisticsOverview> overview() {
        return ApiResponse.success(statisticsService.overview());
    }
}
