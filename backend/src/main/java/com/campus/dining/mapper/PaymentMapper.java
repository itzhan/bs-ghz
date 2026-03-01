package com.campus.dining.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.campus.dining.entity.Payment;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface PaymentMapper extends BaseMapper<Payment> {
}
