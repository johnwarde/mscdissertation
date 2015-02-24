/**
 * 
 */
package com.interop.processor;

/**
 * @author johnwarde
 *
 */
/*
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.rabbit.listener.adapter.MessageListenerAdapter;
*/

import java.util.Map;

import javax.annotation.PostConstruct;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.*;
import org.springframework.boot.autoconfigure.*;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.ScheduledAnnotationBeanPostProcessor;
import org.springframework.stereotype.*;
import org.springframework.web.bind.annotation.*;

//import com.interop.webapp.WebApp;
import com.rabbitmq.client.*;
import com.rabbitmq.client.AMQP.BasicProperties;


@RestController
@EnableAutoConfiguration
public class ProcessorApp {
	@Autowired
	private ProcessorConfig config;

	static Logger log = Logger.getLogger(ProcessorApp.class.getName());
	
/*
    
	@Autowired
	private AmqpTemplate amqpTemplate;

	@Autowired
	private ConnectionFactory connectionFactory;

	@Bean
	public ScheduledAnnotationBeanPostProcessor scheduledAnnotationBeanPostProcessor() {
		return new ScheduledAnnotationBeanPostProcessor();
	}

	@Bean
	public Sender mySender() {
		return new Sender();
	}

	@Bean
	public SimpleMessageListenerContainer container() {
		SimpleMessageListenerContainer container = new SimpleMessageListenerContainer(
				this.connectionFactory);
		Object listener = new Object() {
			@SuppressWarnings("unused")
			public void handleMessage(String foo) {
				System.out.println(foo);
			}
		};
		MessageListenerAdapter adapter = new MessageListenerAdapter(listener);
		container.setMessageListener(adapter);
		container.setQueueNames("foo");
		return container;
	}    
*/
	
    @RequestMapping("/")
    String home() {
        return "Hello World!";
    }


   	@PostConstruct
    public void setUpProcessor() throws Exception {

		JsonWrapper objJson = new JsonWrapper();
		EffectsApplicator effects = new EffectsApplicator();
		
    	ConnectionFactory factory = new ConnectionFactory();
    	factory.setHost(config.getQueueHostName());
    	Connection connection = factory.newConnection();
    	Channel channel = connection.createChannel();
    	channel.queueDeclare(config.getQueueName(), false, false, false, null);
    	channel.basicQos(1);
    	QueueingConsumer consumer = new QueueingConsumer(channel);
    	channel.basicConsume(config.getQueueName(), false, consumer);
    	
		log.info(String.format("processorstart\tsuccess\t%s\t%s", config.getQueueHostName(), config.getQueueName()));	    	
    	while (true) {
    		String status = "";
    		String response = "";
    	    QueueingConsumer.Delivery delivery = consumer.nextDelivery();
    	    BasicProperties props = delivery.getProperties();
    	    BasicProperties replyProps = new BasicProperties
                                     .Builder()
                                     .correlationId(props.getCorrelationId())
                                     .build();
    	    
    	    String message = new String(delivery.getBody());
    	    Map<String, Object> map = objJson.fromJson(message);
    	    if (map != null) {
				log.info(String.format("processrequest\tsuccess\t%s\t%s\t%s", 
						config.getQueueHostName(), map.get("user"), props.getCorrelationId()));			
	
				status = effects.apply((String) map.get("effectName"),
						(String) map.get("inputPath"),
						(String) map.get("ouputPath"));
				
				map.put("status", status);
				map.put("requestCompleted", "TODO:");
				response = objJson.toJson(map);
	
	    		log.info(String.format("processfinish\tsuccess\t%s\t%s\t%s\t%s", 
	    				config.getQueueHostName(), map.get("user"), props.getCorrelationId(),
	    				map.get("ouputPath")));
    	    } else {
    	    	response = "";
    	    }
    	    channel.basicPublish("", props.getReplyTo(), replyProps, response.getBytes());
    	    channel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);
    		log.info(String.format("processcompleted\t%s\t%s\t%s\t%s", status, 
    				config.getQueueHostName(), map.get("user"), props.getCorrelationId()));			
    	}    	

    }
   	
    public static void main(String[] args) throws Exception {
		new SpringApplicationBuilder(ProcessorApp.class, 
				"classpath:/META-INF/application-context.xml").run(args);
    }

}
