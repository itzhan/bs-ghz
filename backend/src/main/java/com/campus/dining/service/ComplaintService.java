package com.campus.dining.service;

import com.campus.dining.common.PageResult;
import com.campus.dining.dto.ComplaintDTO;
import com.campus.dining.entity.Complaint;

public interface ComplaintService {

    /**
     * 创建投诉
     */
    Complaint createComplaint(Long userId, ComplaintDTO dto);

    /**
     * 分页查询用户投诉
     */
    PageResult<Complaint> listComplaintsByUser(Long userId, int page, int size);

    /**
     * 分页查询所有投诉（管理员）
     */
    PageResult<Complaint> listAllComplaints(int page, int size, Integer status);

    /**
     * 处理投诉
     */
    void handleComplaint(Long id, Long handlerId, Integer status, String replyContent);
}
