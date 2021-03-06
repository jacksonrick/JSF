package com.jsf;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * User: xujunfei
 * Date: 2019-03-19
 * Time: 11:55
 */
@SpringBootApplication(scanBasePackages = "com.jsf")
public class DubboProducerApplication {

    public static void main(String[] args) {
        SpringApplication.run(DubboProducerApplication.class, args);
    }

}
