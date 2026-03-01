package com.campus.dining.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.campus.dining.entity.OrderItem;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface OrderItemMapper extends BaseMapper<OrderItem> {
}
