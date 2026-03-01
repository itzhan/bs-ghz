package com.campus.dining.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.campus.dining.common.PageResult;
import com.campus.dining.dto.ComplaintDTO;
import com.campus.dining.entity.Complaint;
import com.campus.dining.exception.BusinessException;
import com.campus.dining.mapper.ComplaintMapper;
import com.campus.dining.service.ComplaintService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ComplaintServiceImpl implements ComplaintService {

    private final ComplaintMapper complaintMapper;

    @Override
    public Complaint createComplaint(Long userId, ComplaintDTO dto) {
        Complaint complaint = new Complaint();
        complaint.setUserId(userId);
        complaint.setStallId(dto.getStallId());
        complaint.setOrderId(dto.getOrderId());
        complaint.setType(dto.getType());
        complaint.setContent(dto.getContent());
        complaint.setImages(dto.getImages());
        complaint.setStatus(0); // 待处理
        complaintMapper.insert(complaint);
        return complaint;
    }

    @Override
    public PageResult<Complaint> listComplaintsByUser(Long userId, int page, int size) {
        LambdaQueryWrapper<Complaint> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Complaint::getUserId, userId)
                .orderByDesc(Complaint::getCreatedAt);

        Page<Complaint> pageResult = complaintMapper.selectPage(new Page<>(page, size), wrapper);
        return PageResult.of(pageResult.getRecords(), pageResult.getTotal(), pageResult.getCurrent(), pageResult.getSize());
    }

    @Override
    public PageResult<Complaint> listAllComplaints(int page, int size, Integer status) {
        LambdaQueryWrapper<Complaint> wrapper = new LambdaQueryWrapper<>();
        if (status != null) {
            wrapper.eq(Complaint::getStatus, status);
        }
        wrapper.orderByDesc(Complaint::getCreatedAt);

        Page<Complaint> pageResult = complaintMapper.selectPage(new Page<>(page, size), wrapper);
        return PageResult.of(pageResult.getRecords(), pageResult.getTotal(), pageResult.getCurrent(), pageResult.getSize());
    }

    @Override
    public void handleComplaint(Long id, Long handlerId, Integer status, String replyContent) {
        Complaint complaint = complaintMapper.selectById(id);
        if (complaint == null) {
            throw new BusinessException("投诉不存在");
        }
        complaint.setHandlerId(handlerId);
        complaint.setStatus(status);
        complaint.setReplyContent(replyContent);
        complaint.setHandledAt(LocalDateTime.now());
        complaintMapper.updateById(complaint);
    }
}
