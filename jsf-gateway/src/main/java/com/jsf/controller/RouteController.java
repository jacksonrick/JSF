package com.jsf.controller;

import com.jsf.service.DynamicRouteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.route.RouteDefinition;
import org.springframework.web.bind.annotation.*;

/**
 * Created with IntelliJ IDEA.
 * Description: 通过接口方式动态修改路由
 * 使用方法及参数见：~/doc/http/gateway.http
 * User: xujunfei
 * Date: 2018-11-26
 * Time: 14:45
 */
@RestController
@RequestMapping("/route")
public class RouteController {

    @Autowired
    private DynamicRouteService dynamicRouteService;

    /**
     * 增加路由
     *
     * @param gwdefinition
     * @return
     */
    @PostMapping("/add")
    public String add(@RequestBody RouteDefinition definition) {
        return this.dynamicRouteService.add(definition);
    }

    /**
     * 删除路由
     *
     * @param id
     * @return
     */
    @GetMapping("/delete/{id}")
    public String delete(@PathVariable String id) {
        return this.dynamicRouteService.delete(id);
    }

    /**
     * 更新路由
     *
     * @param gwdefinition
     * @return
     */
    @PostMapping("/update")
    public String update(@RequestBody RouteDefinition definition) {
        return this.dynamicRouteService.update(definition);
    }

}
