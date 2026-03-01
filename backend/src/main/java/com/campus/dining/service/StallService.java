package com.campus.dining.service;

import com.campus.dining.common.PageResult;
import com.campus.dining.dto.StallDTO;
import com.campus.dining.entity.Stall;

import java.util.List;

public interface StallService {

    /**
     * 创建档口
     */
    Stall createStall(Long merchantId, StallDTO dto);

    /**
     * 更新档口信息
     */
    Stall updateStall(Long id, StallDTO dto);

    /**
     * 根据ID获取档口
     */
    Stall getStallById(Long id);

    /**
     * 分页查询档口列表
     */
    PageResult<Stall> listStalls(int page, int size, String keyword, Integer status);

    /**
     * 获取商户的档口列表
     */
    List<Stall> getStallsByMerchant(Long merchantId);

    /**
     * 管理员审核档口（通过/驳回）
     */
    void auditStall(Long id, Integer status);

    /**
     * 商户更新档口状态（营业/休息/关闭）
     */
    void updateStallStatus(Long id, Integer status);
}
