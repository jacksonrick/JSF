package com.jsf;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "com.jsf")
public class DubboCloudProducerApplication {

    public static void main(String[] args) {
        SpringApplication.run(DubboCloudProducerApplication.class, args);
    }

}
