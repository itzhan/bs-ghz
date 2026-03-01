package com.campus.dining.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.campus.dining.entity.SysConfig;
import com.campus.dining.exception.BusinessException;
import com.campus.dining.mapper.SysConfigMapper;
import com.campus.dining.service.SysConfigService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SysConfigServiceImpl implements SysConfigService {

    private final SysConfigMapper sysConfigMapper;

    @Override
    public SysConfig getByKey(String key) {
        return sysConfigMapper.selectOne(
                new LambdaQueryWrapper<SysConfig>()
                        .eq(SysConfig::getConfigKey, key)
        );
    }

    @Override
    public void updateConfig(String key, String value) {
        SysConfig config = sysConfigMapper.selectOne(
                new LambdaQueryWrapper<SysConfig>()
                        .eq(SysConfig::getConfigKey, key)
        );
        if (config == null) {
            throw new BusinessException("配置项不存在：" + key);
        }
        config.setConfigValue(value);
        sysConfigMapper.updateById(config);
    }

    @Override
    public List<SysConfig> listConfigs() {
        return sysConfigMapper.selectList(null);
    }
}
