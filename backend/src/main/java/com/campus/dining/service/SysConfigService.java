package com.campus.dining.service;

import com.campus.dining.entity.SysConfig;

import java.util.List;

public interface SysConfigService {

    /**
     * 根据key获取配置
     */
    SysConfig getByKey(String key);

    /**
     * 更新配置
     */
    void updateConfig(String key, String value);

    /**
     * 获取所有配置
     */
    List<SysConfig> listConfigs();
}
