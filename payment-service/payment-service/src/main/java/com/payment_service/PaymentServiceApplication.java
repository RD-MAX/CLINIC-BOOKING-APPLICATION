package com.payment_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

// ✅ Correct excludes for Spring Boot 4.x
@SpringBootApplication(
		excludeName = {
				"org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration",
				"org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration"
		}
)
@EnableDiscoveryClient
@EnableFeignClients(basePackages = "com.payment_service.client")
public class PaymentServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(PaymentServiceApplication.class, args);
	}
}