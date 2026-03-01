package com.campus.dining.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.campus.dining.entity.Complaint;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ComplaintMapper extends BaseMapper<Complaint> {
}
