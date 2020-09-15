package com.jsf;

import com.jsf.utils.system.LogManager;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication(scanBasePackages = "com.jsf")
@EnableAsync
//@EnableJMX
//@EnableCache
public class WebManageApplication {

    public static void main(String[] args) {
        LogManager.startup("WebManageApplication");
        SpringApplication.run(WebManageApplication.class, args);
    }

}
