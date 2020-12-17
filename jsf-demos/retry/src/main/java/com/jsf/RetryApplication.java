package com.jsf;

import com.jsf.service.TaskService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.retry.annotation.EnableRetry;

@SpringBootApplication(scanBasePackages = "com.jsf")
@EnableRetry(proxyTargetClass = true)
public class RetryApplication {

    public static void main(String[] args) throws Exception {
        ConfigurableApplicationContext context = SpringApplication.run(RetryApplication.class, args);
        TaskService service = context.getBean("taskService", TaskService.class);
        service.start();
    }

}
