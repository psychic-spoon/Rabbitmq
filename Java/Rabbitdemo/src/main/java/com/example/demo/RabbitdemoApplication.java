package com.example.demo;

import org.springframework.amqp.core.Queue;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
@EnableScheduling
public class RabbitdemoApplication {

	public static final String QUEUE_NAME="spring-boot";
	public static final String ROUTING_KEY="spring-boot";
	
	@Bean
	public RestTemplate getRestTemplate() {
		return new RestTemplate();
	}
	
	@Bean
	public Queue defaultQueue() {
		return new Queue(QUEUE_NAME);
	}
	
	public static void main(String[] args) {
		SpringApplication.run(RabbitdemoApplication.class, args);
		
	}

}
