package com.jsf;

import com.alibaba.csp.sentinel.slots.block.degrade.DegradeException;
import com.alibaba.csp.sentinel.slots.block.flow.FlowException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.UndeclaredThrowableException;

/**
 * 统一异常捕获类
 **/
@ControllerAdvice
public class GlobalException {

    private static final Logger log = LoggerFactory.getLogger(GlobalException.class);

    /**
     * 捕捉Exception异常
     *
     * @param exception
     * @param response
     * @return
     */
    @ExceptionHandler(Exception.class)
    @ResponseBody
    public String exception(Exception exception, HttpServletResponse response) {
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        log.error("GlobalException: " + exception.getClass().getSimpleName());
        return "SERVER_ERROR";
    }

    /**
     * 这里一般用在限流等异常上
     *
     * @param exception
     * @return
     */
    @ExceptionHandler(UndeclaredThrowableException.class)
    @ResponseBody
    public String exception2(UndeclaredThrowableException exception, HttpServletResponse response) {
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        log.warn("BlockException");
        Throwable throwable = exception.getUndeclaredThrowable();
        if (throwable instanceof FlowException) { //限流
            return "SENTINEL_FLOW";
        } else if (throwable instanceof DegradeException) { //熔断
            return "SENTINEL_DEGRADE";
        } else {
            return "ERROR";
        }
    }
}
