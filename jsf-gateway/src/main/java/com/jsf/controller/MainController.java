package com.jsf.controller;

import com.jsf.config.ResMsg;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * User: xujunfei
 * Date: 2021-02-03
 * Time: 13:26
 */
@RestController
public class MainController {

    /**
     * 默认降级处理
     *
     * @return
     */
    @RequestMapping(value = "/defaultfallback")
    public ResMsg defaultfallback() {
        return new ResMsg(-1, "服务不可用");
    }

}
