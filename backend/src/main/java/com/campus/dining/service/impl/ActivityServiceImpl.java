package com.campus.dining.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.campus.dining.common.Constants;
import com.campus.dining.common.PageResult;
import com.campus.dining.dto.ActivityDTO;
import com.campus.dining.entity.Activity;
import com.campus.dining.exception.BusinessException;
import com.campus.dining.mapper.ActivityMapper;
import com.campus.dining.service.ActivityService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ActivityServiceImpl implements ActivityService {

    private final ActivityMapper activityMapper;

    @Override
    public Activity create(ActivityDTO dto) {
        Activity activity = new Activity();
        activity.setStallId(dto.getStallId());
        activity.setTitle(dto.getTitle());
        activity.setDescription(dto.getDescription());
        activity.setImage(dto.getImage());
        activity.setDiscountType(dto.getDiscountType());
        activity.setDiscountValue(dto.getDiscountValue());
        activity.setMinAmount(dto.getMinAmount());
        activity.setStartTime(dto.getStartTime());
        activity.setEndTime(dto.getEndTime());
        activity.setStatus(Constants.ACTIVITY_NOT_STARTED);
        activityMapper.insert(activity);
        return activity;
    }

    @Override
    public Activity update(Long id, ActivityDTO dto) {
        Activity activity = activityMapper.selectById(id);
        if (activity == null) {
            throw new BusinessException("活动不存在");
        }
        if (StringUtils.hasText(dto.getTitle())) {
            activity.setTitle(dto.getTitle());
        }
        if (dto.getDescription() != null) {
            activity.setDescription(dto.getDescription());
        }
        if (dto.getImage() != null) {
            activity.setImage(dto.getImage());
        }
        if (dto.getDiscountType() != null) {
            activity.setDiscountType(dto.getDiscountType());
        }
        if (dto.getDiscountValue() != null) {
            activity.setDiscountValue(dto.getDiscountValue());
        }
        if (dto.getMinAmount() != null) {
            activity.setMinAmount(dto.getMinAmount());
        }
        if (dto.getStartTime() != null) {
            activity.setStartTime(dto.getStartTime());
        }
        if (dto.getEndTime() != null) {
            activity.setEndTime(dto.getEndTime());
        }
        activityMapper.updateById(activity);
        return activity;
    }

    @Override
    public void delete(Long id) {
        Activity activity = activityMapper.selectById(id);
        if (activity == null) {
            throw new BusinessException("活动不存在");
        }
        activityMapper.deleteById(id);
    }

    @Override
    public Activity getById(Long id) {
        Activity activity = activityMapper.selectById(id);
        if (activity == null) {
            throw new BusinessException("活动不存在");
        }
        return activity;
    }

    @Override
    public PageResult<Activity> listActivities(int page, int size, Long stallId, Integer status) {
        LambdaQueryWrapper<Activity> wrapper = new LambdaQueryWrapper<>();
        if (stallId != null) {
            wrapper.eq(Activity::getStallId, stallId);
        }
        if (status != null) {
            wrapper.eq(Activity::getStatus, status);
        }
        wrapper.orderByDesc(Activity::getCreatedAt);

        Page<Activity> pageResult = activityMapper.selectPage(new Page<>(page, size), wrapper);
        return PageResult.of(pageResult.getRecords(), pageResult.getTotal(), pageResult.getCurrent(), pageResult.getSize());
    }

    @Override
    public List<Activity> listActiveByStall(Long stallId) {
        LocalDateTime now = LocalDateTime.now();
        return activityMapper.selectList(
                new LambdaQueryWrapper<Activity>()
                        .eq(Activity::getStallId, stallId)
                        .eq(Activity::getStatus, Constants.ACTIVITY_ONGOING)
                        .le(Activity::getStartTime, now)
                        .ge(Activity::getEndTime, now)
        );
    }
}
