package com.jsf;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.hystrix.EnableHystrix;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication(scanBasePackages = "com.jsf")
@EnableDiscoveryClient
@EnableFeignClients
@EnableHystrix
public class MSRegisterApplication {

    public static void main(String[] args) {
        SpringApplication.run(MSRegisterApplication.class, args);
    }

}
