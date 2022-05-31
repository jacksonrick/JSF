package com.jsf.controller;

import com.jsf.service.DataService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * Created with IntelliJ IDEA.
 * Description: SpringCloud配置管理
 * maven依赖使用 spring-cloud-starter-alibaba-nacos-config
 * User: xujunfei
 * Date: 2020-06-12
 * Time: 17:09
 */
@RestController
@RefreshScope
public class SpringCloudController {

    @Value("${config.appName}")
    private String appName;

    @Resource
    private DataService dataService;

    @GetMapping("/get")
    public Object get() {
        return appName;
    }

    @GetMapping("/getUrl")
    public Object getUrl() {
        return dataService.getUrl();
    }

    @GetMapping("/get/{id}")
    public Object get(@PathVariable("id") Integer id) {
        return dataService.get(id);
    }

}
