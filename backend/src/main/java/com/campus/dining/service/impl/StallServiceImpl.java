package com.campus.dining.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.campus.dining.common.PageResult;
import com.campus.dining.dto.StallDTO;
import com.campus.dining.entity.Stall;
import com.campus.dining.exception.BusinessException;
import com.campus.dining.mapper.StallMapper;
import com.campus.dining.service.StallService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StallServiceImpl implements StallService {

    private final StallMapper stallMapper;

    @Override
    public Stall createStall(Long merchantId, StallDTO dto) {
        Stall stall = new Stall();
        stall.setMerchantId(merchantId);
        stall.setName(dto.getName());
        stall.setDescription(dto.getDescription());
        stall.setLogo(dto.getLogo());
        stall.setLocation(dto.getLocation());
        stall.setPhone(dto.getPhone());
        stall.setBusinessHours(dto.getBusinessHours());
        stall.setStatus(0); // 待审核
        stall.setAvgRating(BigDecimal.ZERO);
        stall.setMonthlySales(0);
        stallMapper.insert(stall);
        return stall;
    }

    @Override
    public Stall updateStall(Long id, StallDTO dto) {
        Stall stall = stallMapper.selectById(id);
        if (stall == null) {
            throw new BusinessException("档口不存在");
        }
        if (StringUtils.hasText(dto.getName())) {
            stall.setName(dto.getName());
        }
        if (dto.getDescription() != null) {
            stall.setDescription(dto.getDescription());
        }
        if (dto.getLogo() != null) {
            stall.setLogo(dto.getLogo());
        }
        if (dto.getLocation() != null) {
            stall.setLocation(dto.getLocation());
        }
        if (dto.getPhone() != null) {
            stall.setPhone(dto.getPhone());
        }
        if (dto.getBusinessHours() != null) {
            stall.setBusinessHours(dto.getBusinessHours());
        }
        stallMapper.updateById(stall);
        return stall;
    }

    @Override
    public Stall getStallById(Long id) {
        Stall stall = stallMapper.selectById(id);
        if (stall == null) {
            throw new BusinessException("档口不存在");
        }
        return stall;
    }

    @Override
    public PageResult<Stall> listStalls(int page, int size, String keyword, Integer status) {
        LambdaQueryWrapper<Stall> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(keyword)) {
            wrapper.like(Stall::getName, keyword);
        }
        if (status != null) {
            wrapper.eq(Stall::getStatus, status);
        }
        wrapper.orderByDesc(Stall::getCreatedAt);

        Page<Stall> pageResult = stallMapper.selectPage(new Page<>(page, size), wrapper);
        return PageResult.of(pageResult.getRecords(), pageResult.getTotal(), pageResult.getCurrent(), pageResult.getSize());
    }

    @Override
    public List<Stall> getStallsByMerchant(Long merchantId) {
        return stallMapper.selectList(
                new LambdaQueryWrapper<Stall>()
                        .eq(Stall::getMerchantId, merchantId)
                        .orderByDesc(Stall::getCreatedAt)
        );
    }

    @Override
    public void auditStall(Long id, Integer status) {
        Stall stall = stallMapper.selectById(id);
        if (stall == null) {
            throw new BusinessException("档口不存在");
        }
        stall.setStatus(status);
        stallMapper.updateById(stall);
    }

    @Override
    public void updateStallStatus(Long id, Integer status) {
        Stall stall = stallMapper.selectById(id);
        if (stall == null) {
            throw new BusinessException("档口不存在");
        }
        stall.setStatus(status);
        stallMapper.updateById(stall);
    }
}
