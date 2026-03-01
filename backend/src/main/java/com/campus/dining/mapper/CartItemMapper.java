package com.campus.dining.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.campus.dining.entity.CartItem;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface CartItemMapper extends BaseMapper<CartItem> {
}
