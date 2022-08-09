package com.jsf;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "com.jsf")
public class DubboCloudConsumerApplication {

    public static void main(String[] args) {
        SpringApplication.run(DubboCloudConsumerApplication.class, args);
    }

}
