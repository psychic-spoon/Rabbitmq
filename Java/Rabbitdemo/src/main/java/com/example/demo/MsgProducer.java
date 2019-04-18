package com.example.demo;

import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class MsgProducer {

	private static final Logger log = LoggerFactory.getLogger(MsgProducer.class);

	private final RabbitTemplate rabbitTemplate;

	public MsgProducer(final RabbitTemplate rabbitTemplate) {
		this.rabbitTemplate = rabbitTemplate;
	}

	@Scheduled(fixedDelay = 30000L)
	public void sendMessage() {
		log.info("Sending message...");
		rabbitTemplate.convertAndSend("spring-boot", "Spring Consumer is Up");
	}
}
