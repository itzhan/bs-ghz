package com.campus.dining.service;

import com.campus.dining.common.PageResult;
import com.campus.dining.dto.ActivityDTO;
import com.campus.dining.entity.Activity;

import java.util.List;

public interface ActivityService {

    /**
     * 创建活动
     */
    Activity create(ActivityDTO dto);

    /**
     * 更新活动
     */
    Activity update(Long id, ActivityDTO dto);

    /**
     * 删除活动
     */
    void delete(Long id);

    /**
     * 根据ID获取活动
     */
    Activity getById(Long id);

    /**
     * 分页查询活动列表
     */
    PageResult<Activity> listActivities(int page, int size, Long stallId, Integer status);

    /**
     * 获取档口进行中的活动
     */
    List<Activity> listActiveByStall(Long stallId);
}
