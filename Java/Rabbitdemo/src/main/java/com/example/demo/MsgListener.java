package com.example.demo;

import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class MsgListener {
	private static final Logger log=LoggerFactory.getLogger(MsgListener.class);
	
	private static String API_ENDPOINT_VALID = "http://sjc5-wasl-prrq1.cisco.com:5033/prrqpy/diff_collection";

	private static String API_ENDPOINT_JOB = "http://sjc5-wasl-prrq1.cisco.com:5033/prrqpy/diff_job";

	@Autowired
	private RestTemplate restTemplate;
	
	@RabbitListener(queues="spring-boot")
	public void consumeMessage(String msg) {
		log.info("Received Message: "+msg);
	}
	
	private ResponseEntity<String> consumerHandler(String url,String jsonString){
		String authToken="aXJ0LmdlbjpJcnRfOTg3Ng==";
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.add("Authorization", "Basic "+authToken);
 
		HttpEntity<String> entity = new HttpEntity<String>(jsonString, headers);
		ResponseEntity<String> response = restTemplate.postForEntity(url, entity,String.class);
		
		return response;
	}
	
	public String parseBytes(String msg) {
		StringBuilder sb=new StringBuilder();
		String[] arr=msg.split(",");
		for(String s:arr) {
			sb.append((char)Integer.parseInt(s));
		}
		return sb.toString();
	}
	
	@RabbitListener(queues="validated_queue")
	public void consumeMessageValid(String msg) {
//		log.info("Received Message: "+Arrays.toString(msg));
		String jsonString = parseBytes(msg);
		log.info(jsonString);
		ResponseEntity<String> response=null;
		try {
			response = consumerHandler(API_ENDPOINT_VALID,jsonString);
		}
		catch(Exception e) {
			log.info("Exception coming");
			log.info(e.toString());
		}
		log.info(response.toString());
	}
	
	@RabbitListener(queues="sbs_queue")
	public void consumeMessageSbs(String msg) {
		String jsonString = parseBytes(msg);
		log.info(jsonString);
		ResponseEntity<String> response = consumerHandler(API_ENDPOINT_JOB,jsonString);
		log.info(response.toString());
	}
}
