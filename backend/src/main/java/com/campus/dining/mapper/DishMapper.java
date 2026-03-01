package com.campus.dining.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.campus.dining.entity.Dish;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface DishMapper extends BaseMapper<Dish> {
}
