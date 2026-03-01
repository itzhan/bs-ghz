package com.campus.dining.controller;

import com.campus.dining.common.Result;
import com.campus.dining.service.impl.StatsServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/stats")
@RequiredArgsConstructor
public class StatsController {

    private final StatsServiceImpl statsService;

    /**
     * 公开仪表板摘要
     */
    @GetMapping("/dashboard")
    public Result<Map<String, Object>> getDashboardStats() {
        return Result.success(statsService.getDashboardStats());
    }

    /**
     * 公开菜品销量排名
     */
    @GetMapping("/dishes/ranking")
    public Result<List<Map<String, Object>>> getDishRanking(
            @RequestParam(required = false) Long stallId,
            @RequestParam(defaultValue = "10") int limit) {
        return Result.success(statsService.getDishRanking(stallId, limit));
    }
}
