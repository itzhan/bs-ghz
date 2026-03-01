package com.campus.dining.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.campus.dining.common.PageResult;
import com.campus.dining.dto.AnnouncementDTO;
import com.campus.dining.entity.Announcement;
import com.campus.dining.exception.BusinessException;
import com.campus.dining.mapper.AnnouncementMapper;
import com.campus.dining.service.AnnouncementService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AnnouncementServiceImpl implements AnnouncementService {

    private final AnnouncementMapper announcementMapper;

    @Override
    public Announcement create(AnnouncementDTO dto, Long publisherId) {
        Announcement announcement = new Announcement();
        announcement.setTitle(dto.getTitle());
        announcement.setContent(dto.getContent());
        announcement.setType(dto.getType() != null ? dto.getType() : 0);
        announcement.setPublisherId(publisherId);
        announcement.setStallId(dto.getStallId());
        announcement.setStatus(0); // 草稿
        announcementMapper.insert(announcement);
        return announcement;
    }

    @Override
    public Announcement update(Long id, AnnouncementDTO dto) {
        Announcement announcement = announcementMapper.selectById(id);
        if (announcement == null) {
            throw new BusinessException("公告不存在");
        }
        if (StringUtils.hasText(dto.getTitle())) {
            announcement.setTitle(dto.getTitle());
        }
        if (StringUtils.hasText(dto.getContent())) {
            announcement.setContent(dto.getContent());
        }
        if (dto.getType() != null) {
            announcement.setType(dto.getType());
        }
        if (dto.getStallId() != null) {
            announcement.setStallId(dto.getStallId());
        }
        announcementMapper.updateById(announcement);
        return announcement;
    }

    @Override
    public void delete(Long id) {
        Announcement announcement = announcementMapper.selectById(id);
        if (announcement == null) {
            throw new BusinessException("公告不存在");
        }
        announcementMapper.deleteById(id);
    }

    @Override
    public Announcement getById(Long id) {
        Announcement announcement = announcementMapper.selectById(id);
        if (announcement == null) {
            throw new BusinessException("公告不存在");
        }
        return announcement;
    }

    @Override
    public PageResult<Announcement> listAnnouncements(int page, int size, Integer type, Integer status) {
        LambdaQueryWrapper<Announcement> wrapper = new LambdaQueryWrapper<>();
        if (type != null) {
            wrapper.eq(Announcement::getType, type);
        }
        if (status != null) {
            wrapper.eq(Announcement::getStatus, status);
        }
        wrapper.orderByDesc(Announcement::getCreatedAt);

        Page<Announcement> pageResult = announcementMapper.selectPage(new Page<>(page, size), wrapper);
        return PageResult.of(pageResult.getRecords(), pageResult.getTotal(), pageResult.getCurrent(), pageResult.getSize());
    }

    @Override
    public void publish(Long id) {
        Announcement announcement = announcementMapper.selectById(id);
        if (announcement == null) {
            throw new BusinessException("公告不存在");
        }
        announcement.setStatus(1); // 已发布
        announcement.setPublishedAt(LocalDateTime.now());
        announcementMapper.updateById(announcement);
    }

    @Override
    public void takeDown(Long id) {
        Announcement announcement = announcementMapper.selectById(id);
        if (announcement == null) {
            throw new BusinessException("公告不存在");
        }
        announcement.setStatus(2); // 已下架
        announcementMapper.updateById(announcement);
    }
}
