package com.jsf;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * User: xujunfei
 * Date: 2019-11-14
 * Time: 10:12
 */
// 请直接运行test包下的测试类
@SpringBootApplication(scanBasePackages = "com.jsf")
public class ShardingDbApplication {

    public static void main(String[] args) {
        SpringApplication.run(ShardingDbApplication.class, args);
    }
}
