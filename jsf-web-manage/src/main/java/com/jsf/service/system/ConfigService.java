package com.jsf.service.system;

import com.jsf.database.mapper.ConfigMapper;
import com.jsf.database.model.custom.IdText;
import com.jsf.database.model.manage.Config;
import com.jsf.system.component.AppRunners;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * User: xujunfei
 * Date: 2018-08-06
 * Time: 11:52
 */
@Service
public class ConfigService {

    @Resource
    private ConfigMapper configMapper;

    @Resource
    private AppRunners runners;

    /**
     * 查询所有配置
     *
     * @return list
     */
    public Map<String, List<Config>> findConfigMapList() {
        Map<String, List<Config>> map = new TreeMap<>();
        List<Config> configs = configMapper.findAll();
        for (Config config : configs) {
            map.computeIfAbsent(config.getGrp(), key -> new ArrayList<>()).add(config);
        }
        return map;
    }

    /**
     * 多个key获取val
     *
     * @param keys new String[]{""}
     * @return
     */
    public Map<String, String> findConfigsByKey(String[] keys) {
        Map<String, String> map = new HashMap<String, String>();
        List<Config> list = configMapper.findByKeys(keys);
        for (Config config : list) {
            map.put(config.getKey(), config.getVal());
        }
        return map;
    }

    /**
     * 更新配置
     *
     * @param map
     * @return
     */
    public void updateConfig(List<IdText> configs) {
        configMapper.updateBatch(configs);
        runners.getSysConfig();
    }

    public void refreshConfig() {
        runners.getSysConfig();
    }

}
