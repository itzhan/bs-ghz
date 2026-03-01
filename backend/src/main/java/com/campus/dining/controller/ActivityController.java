package com.campus.dining.controller;

import com.campus.dining.common.PageResult;
import com.campus.dining.common.Result;
import com.campus.dining.entity.Activity;
import com.campus.dining.service.ActivityService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/activities")
@RequiredArgsConstructor
public class ActivityController {

    private final ActivityService activityService;

    /**
     * 分页查询活动列表（公开）
     */
    @GetMapping
    public Result<PageResult<Activity>> listActivities(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) Long stallId,
            @RequestParam(required = false) Integer status) {
        return Result.success(activityService.listActivities(page, size, stallId, status));
    }

    /**
     * 获取活动详情（公开）
     */
    @GetMapping("/{id}")
    public Result<Activity> getActivity(@PathVariable Long id) {
        return Result.success(activityService.getById(id));
    }
}
