package com.jsf;

import com.jsf.system.conf.annotation.EnableCache;
import com.jsf.utils.system.LogManager;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "com.jsf")
@EnableCache
public class WebApiApplication {

	public static void main(String[] args) {
		LogManager.startup("WebApiApplication");
		SpringApplication.run(WebApiApplication.class, args);
	}
}
