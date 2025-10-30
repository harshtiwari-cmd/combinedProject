package com.digi.common;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableFeignClients(basePackages = { "com.*" })
@EnableJpaRepositories(basePackages = { "com.*" })
@EntityScan(basePackages = { "com.*" })
@ComponentScan(basePackages = { "com.*" })
public class CommonServiceV2Application {

	public static void main(String[] args) {
		SpringApplication.run(CommonServiceV2Application.class, args);
	}

}
