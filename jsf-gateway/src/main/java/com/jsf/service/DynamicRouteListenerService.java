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

import java.util.concurrent.Executor;

/**
 * Created with IntelliJ IDEA.
 * Description: 监听Nacos Server下发的动态路由配置
 * User: xujunfei
 * Date: 2018-11-26
 * Time: 13:48
 */
@Configuration
public class DynamicRouteListenerService implements InitializingBean {

    private static final Logger logger = LoggerFactory.getLogger(DynamicRouteListenerService.class);

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
        // 配置格式为JSON
        String configInfo = nacosConfigManager.getConfigService().getConfigAndSignListener(appName + ".json", configProperties.getGroup(), 5000, new Listener() {
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
        logger.info("获取到配置：\n" + configInfo);
        RouteDefinition definition = JSON.parseObject(configInfo, RouteDefinition.class);
        dynamicRouteService.update(definition);
    }

}
