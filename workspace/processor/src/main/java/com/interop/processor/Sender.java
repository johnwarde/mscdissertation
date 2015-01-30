/**
 * 
 */
package com.interop.processor;

/**
 * @author johnwarde
 *
 */
import javax.annotation.PostConstruct;

import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;

public class Sender {

	@Autowired
	private RabbitTemplate rabbitTemplate;

	@Autowired
	private AmqpAdmin amqpAdmin;

	@PostConstruct
	public void setUpQueue() {
		this.amqpAdmin.declareQueue(new Queue("foo"));
	}

	@Scheduled(fixedDelay = 1000L)
	public void send() {
		this.rabbitTemplate.convertAndSend("foo", "hello");
	}

}
