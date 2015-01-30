/**
 * 
 */
package processor;

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

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.*;
import org.springframework.boot.autoconfigure.*;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.ScheduledAnnotationBeanPostProcessor;
import org.springframework.stereotype.*;
import org.springframework.web.bind.annotation.*;

//import com.interop.webapp.WebApp;
import com.rabbitmq.client.*;
import com.rabbitmq.client.AMQP.BasicProperties;

//@RestController
//@EnableAutoConfiguration
public class ProcessorApp {

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

    @RequestMapping("/")
    String home() {
        return "Hello World!";
    }
	
*/
	static Logger log = Logger.getLogger(ProcessorApp.class.getName());

	private static int fib(int n) throws Exception {
	    if (n == 0) return 0;
	    if (n == 1) return 1;
	    return fib(n-1) + fib(n-2);
	}
	private static final String RPC_QUEUE_NAME = "rpc_queue";
	private static final String HOST_NAME = "localhost";
	
    public static void main(String[] args) throws Exception {
        //SpringApplication.run(ProcessorApp.class, args);

    	ConnectionFactory factory = new ConnectionFactory();
    	factory.setHost(HOST_NAME);

    	Connection connection = factory.newConnection();
    	Channel channel = connection.createChannel();

    	channel.queueDeclare(RPC_QUEUE_NAME, false, false, false, null);

    	channel.basicQos(1);

    	QueueingConsumer consumer = new QueueingConsumer(channel);
    	channel.basicConsume(RPC_QUEUE_NAME, false, consumer);

		log.info(String.format("processorstart\tsuccess\t%s", HOST_NAME));			
    	
//    	System.out.println(" [x] Awaiting RPC requests");

    	while (true) {
    	    QueueingConsumer.Delivery delivery = consumer.nextDelivery();

    	    BasicProperties props = delivery.getProperties();
    	    BasicProperties replyProps = new BasicProperties
    	                                     .Builder()
    	                                     .correlationId(props.getCorrelationId())
    	                                     .build();

    	    String message = new String(delivery.getBody());
    		log.info(String.format("processrequest\tsuccess\t%s\t%s\t%s", HOST_NAME, "TODO:user", props.getCorrelationId()));			

    	    int n = Integer.parseInt(message);
    	    System.out.println(" [.] fib(" + message + ")");
    	    String response = "" + fib(n);
    	    Thread.sleep(14000);

    		log.info(String.format("processfinish\tsuccess\t%s\t%s\t%s", HOST_NAME, "TODO:user", props.getCorrelationId()));			
    	    channel.basicPublish( "", props.getReplyTo(), replyProps, response.getBytes());
    	    channel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);
    		log.info(String.format("processcompleted\tsuccess\t%s\t%s\t%s", HOST_NAME, "TODO:user", props.getCorrelationId()));			
    	}    	
        
    }

}
