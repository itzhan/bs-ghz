package com.campus.dining.service;

import com.campus.dining.common.PageResult;
import com.campus.dining.dto.AnnouncementDTO;
import com.campus.dining.entity.Announcement;

public interface AnnouncementService {

    /**
     * 创建公告
     */
    Announcement create(AnnouncementDTO dto, Long publisherId);

    /**
     * 更新公告
     */
    Announcement update(Long id, AnnouncementDTO dto);

    /**
     * 删除公告
     */
    void delete(Long id);

    /**
     * 根据ID获取公告
     */
    Announcement getById(Long id);

    /**
     * 分页查询公告列表
     */
    PageResult<Announcement> listAnnouncements(int page, int size, Integer type, Integer status);

    /**
     * 发布公告
     */
    void publish(Long id);

    /**
     * 下架公告
     */
    void takeDown(Long id);
}
