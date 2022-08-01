package com.jsf;

import com.alibaba.csp.sentinel.annotation.aspectj.SentinelResourceAspect;
import com.alibaba.csp.sentinel.datasource.ReadableDataSource;
import com.alibaba.csp.sentinel.datasource.nacos.NacosDataSource;
import com.alibaba.csp.sentinel.slots.block.degrade.DegradeRule;
import com.alibaba.csp.sentinel.slots.block.degrade.DegradeRuleManager;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRule;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRuleManager;
import com.alibaba.fastjson.JSON;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * Description: 降级/限流策略
 * User: xujunfei
 * Date: 2022-06-21
 * Time: 13:12
 */
@Component
public class SentinelConfig {

    private static final Logger log = LoggerFactory.getLogger(SentinelConfig.class);

    // 注解支持
    @Bean
    public SentinelResourceAspect sentinelResourceAspect() {
        return new SentinelResourceAspect();
    }

    @PostConstruct
    public void init() {
        log.info("加载限流熔断规则");
        // 限流
        List<FlowRule> flowRules = new ArrayList<>();
        flowRules.add((
                new FlowRule("myflow")
                        .setGrade(1)  //0: thread count, 1: QPS
                        .setCount(10)
        ));
        FlowRuleManager.loadRules(flowRules);

        // 熔断
        List<DegradeRule> degradeRules = new ArrayList<>();
        degradeRules.add((
                new DegradeRule("mydegrade")
                        .setGrade(2) //0: average RT, 1: exception ratio, 2: exception count
                        .setCount(5)
                        .setTimeWindow(3)
        ));
        DegradeRuleManager.loadRules(degradeRules);
    }

    //@PostConstruct
    public void initFromNacos() {
        log.info("从Nacos加载限流熔断规则");
        ReadableDataSource<String, List<FlowRule>> flowRuleDataSource = new NacosDataSource<>(
                "dev:8848", "DEFAULT_GROUP", "sentinel_config",
                source -> JSON.parseArray(source, FlowRule.class));
        FlowRuleManager.register2Property(flowRuleDataSource.getProperty());
        // 监听FlowRuleManager.FlowPropertyListener.configUpdate()

        // 配置示例：
        //[
        //    {
        //        "resource": "myflow",
        //        "grade": 1,
        //        "count": 10
        //    },
        //    {
        //        "resource": "mydegrade",
        //        "grade": 2,
        //        "count": 5,
        //        "timeWindow": 3
        //    }
        //]
    }

}
