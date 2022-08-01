package com.jsf.service;

import com.alibaba.cloud.nacos.NacosConfigManager;
import com.alibaba.cloud.nacos.NacosConfigProperties;
import com.alibaba.fastjson.JSON;
import com.alibaba.nacos.api.config.listener.Listener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.route.RouteDefinition;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.concurrent.Executor;

/**
 * Created with IntelliJ IDEA.
 * Description: 监听Nacos Server下发的动态路由配置
 * User: xujunfei
 * Date: 2018-11-26
 * Time: 13:48
 */
@Configuration
public class DynamicRouteListener implements InitializingBean {

    private static final Logger logger = LoggerFactory.getLogger(DynamicRouteListener.class);

    @Value("${spring.application.name}")
    private String appName;

    @Autowired
    private NacosConfigManager nacosConfigManager;
    @Autowired
    private NacosConfigProperties configProperties;

    @Autowired
    private DynamicRouteService dynamicRouteService;


    @Override
    public void afterPropertiesSet() throws Exception {
        // 添加监听器
        // 配置格式为JSON，dataId为[appName].json
        String configInfo = nacosConfigManager.getConfigService().getConfigAndSignListener(
                appName + ".json", configProperties.getGroup(), 5000, new Listener() {
                    @Override
                    public Executor getExecutor() {
                        return null;
                    }

                    @Override
                    public void receiveConfigInfo(String configInfo) {
                        update(configInfo);
                    }
                });
        update(configInfo);
    }

    private void update(String configInfo) {
        logger.info("读取Nacos动态路由配置：\n" + configInfo);
        List<RouteDefinition> definitions = JSON.parseArray(configInfo, RouteDefinition.class);
        logger.info("共读取到" + definitions.size() + "项配置");
        for (RouteDefinition definition : definitions) {
            dynamicRouteService.update(definition);
        }
    }

}
