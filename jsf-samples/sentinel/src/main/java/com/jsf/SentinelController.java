package com.jsf;

import com.alibaba.csp.sentinel.annotation.SentinelResource;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Random;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * User: xujunfei
 * Date: 2022-06-21
 * Time: 15:21
 */
@RestController
public class SentinelController {

    /**
     * 限流测试，全局异常捕捉
     */
    @GetMapping(value = "/test1")
    @SentinelResource(value = "myflow")
    public String test1() {
        return "SUCCESS";
    }

    /**
     * 熔断测试，全局异常捕捉
     */
    @GetMapping(value = "/test2")
    @SentinelResource(value = "mydegrade")
    public String test2() {
        if (new Random().nextInt(10) >= 5) {
            throw new IllegalArgumentException("myerror");
        }
        return "SUCCESS";
    }

    /**
     * 兜底测试，此时不会被全局异常捕捉到
     *
     * <p>
     * blockHandler: 指定Sentinel规则异常(flow+degrade)兜底逻辑具体哪个方法
     * blockHandlerClass: 指定Sentinel规则异常兜底逻辑所在class类，不指定为本类
     * fallback: 指定Java运行时异常兜底逻辑具体哪个方法
     * fallbackClass: 指定Java运行时异常兜底逻辑所在class类，不指定为本类
     * </p>
     */
    @GetMapping(value = "/test3")
    @SentinelResource(value = "myflow", blockHandler = "test3Block", fallback = "test3Fallback")
    public String test3(Integer id) {
        // System.out.println(1 / 0); // fallback测试
        return "SUCCESS";
    }

    /**
     * sentinel规则异常兜底方法
     * 若使用自定义的兜底方法，出入参数要与原方法一致，且最后一个参数为BlockException
     */
    public String test3Block(Integer id, BlockException e) {
        System.out.println("======================" + e.getClass().getSimpleName());
        return "MY BLOCK";
    }

    /**
     * java异常时兜底方法
     * 这里不用加BlockException
     */
    public String test3Fallback(Integer id) {
        return "MY FALLBACK";
    }

}
