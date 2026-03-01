package com.campus.dining.controller;

import com.campus.dining.common.PageResult;
import com.campus.dining.common.Result;
import com.campus.dining.entity.Announcement;
import com.campus.dining.service.AnnouncementService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/announcements")
@RequiredArgsConstructor
public class AnnouncementController {

    private final AnnouncementService announcementService;

    /**
     * 分页查询已发布公告（公开）
     */
    @GetMapping
    public Result<PageResult<Announcement>> listAnnouncements(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) Integer type) {
        return Result.success(announcementService.listAnnouncements(page, size, type, 1));
    }

    /**
     * 获取公告详情（公开）
     */
    @GetMapping("/{id}")
    public Result<Announcement> getAnnouncement(@PathVariable Long id) {
        return Result.success(announcementService.getById(id));
    }
}
