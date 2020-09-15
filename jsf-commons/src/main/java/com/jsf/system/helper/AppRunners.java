package com.jsf.system.helper;

import com.jsf.database.mapper.ConfigMapper;
import com.jsf.database.model.manage.Config;
import com.jsf.system.conf.SysConfig;
import com.jsf.utils.json.JacksonUtil;
import com.jsf.utils.system.LogManager;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * Description: 应用启动后执行方法
 * User: xujunfei
 * Date: 2018-04-17
 * Time: 10:35
 */
@Component
//@Order(1)
public class AppRunners implements ApplicationRunner {

    @Override
    public void run(ApplicationArguments args) throws Exception {
        getSysConfig();
    }


    @Resource
    private ConfigMapper configMapper;

    /**
     * 获取系统配置
     * From DB
     */
    public void getSysConfig() {
        LogManager.info("[AppRunner] getSysConfig");
        List<Config> configs = configMapper.findAll();
        for (Config config : configs) {
            switch (config.getType()) {
                case "string":
                    SysConfig.CONFIGS.put(config.getGrp() + "." + config.getKey(), config.getVal());
                    break;
                case "boolean":
                    SysConfig.CONFIGS.put(config.getGrp() + "." + config.getKey(), Boolean.valueOf(config.getVal()));
                    break;
                case "int":
                    SysConfig.CONFIGS.put(config.getGrp() + "." + config.getKey(), Integer.valueOf(config.getVal()));
                    break;
                case "long":
                    SysConfig.CONFIGS.put(config.getGrp() + "." + config.getKey(), Long.valueOf(config.getVal()));
                    break;
                case "json":
                    SysConfig.CONFIGS.put(config.getGrp() + "." + config.getKey(), JacksonUtil.jsonToMap(config.getVal()));
                    break;
                case "list":
                    String[] strs = config.getVal().split(",");
                    SysConfig.CONFIGS.put(config.getGrp() + "." + config.getKey(), Arrays.asList(strs));
                    break;
                case "array":
                    SysConfig.CONFIGS.put(config.getGrp() + "." + config.getKey(), config.getVal().split(","));
                    break;
            }
        }
    }

}
