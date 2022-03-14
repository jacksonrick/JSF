package com.jsf.config;

import com.jsf.database.model.ResMsg;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

@ControllerAdvice
public class ExceptionAdvice {

    /**
     * 通常微服务开发中，约定大于规范
     * 我们定义返回类为ResMsg，code=0时为正常，小于0系统异常，大于0业务异常
     * http状态码不作参考
     */

    @Value("${spring.application.name}")
    private String appName;

    private static final Logger log = LoggerFactory.getLogger(ExceptionAdvice.class);

    @ExceptionHandler({Exception.class})
    @ResponseBody
    public ResMsg handleException(HttpServletRequest request, Exception e) {
        log.error("###### 全局异常 ######", e);
        return new ResMsg(-1, "[" + appName + "]服务错误");
    }

}
