/*
 * Copyright 2012-2014 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.interop.webapp;

import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.nio.file.StandardCopyOption.*;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;

import org.apache.log4j.Logger;

import com.rabbitmq.client.*;
import com.rabbitmq.client.AMQP.BasicProperties;

/*
import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
*/
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.config.annotation.ContentNegotiationConfigurer;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

@EnableAutoConfiguration
@ComponentScan
@Controller
public class WebApp extends WebMvcConfigurerAdapter {
	@Autowired
	private WebAppConfig config;
	
	static String imagesWebPath = "resources";
	static String processedFilesWebPath = "processedfiles";
	static String imagesWebPathMask = "/" + imagesWebPath + "/**";

	static Logger log = Logger.getLogger(WebApp.class.getName());

	private Connection connection;
	private Channel channel;
	private String requestQueueName = "processor_rpc_queue";
	private String replyQueueName;
	private QueueingConsumer consumer;	
	
    @Override
    public void configureContentNegotiation(
            ContentNegotiationConfigurer configurer) {
        configurer.favorPathExtension(false);
    }	

	@PostConstruct
	public void setUpQueue() throws Exception {

	    ConnectionFactory factory = new ConnectionFactory();
	    factory.setHost("localhost");
	    connection = factory.newConnection();
	    channel = connection.createChannel();

	    replyQueueName = channel.queueDeclare().getQueue(); 
	    consumer = new QueueingConsumer(channel);
	    channel.basicConsume(replyQueueName, true, consumer);	    
	}


	/**
	 * @return String - logged in user
	 */
	private String getLoggedInUser() {
		return SecurityContextHolder.getContext().getAuthentication().getName();
	}

	
	@RequestMapping("/")
	public String home(Map<String, Object> model) {
	    String user = getLoggedInUser();
		UserImageFileRepository store = 
				new UserImageFileRepository(user, config.getImageFilesRoot());
		List<ImageDetailBean> collection = store.getWebPaths();
		model.put("user", user);
		model.put("imagesWebPath", imagesWebPath);
		model.put("imagerefs", collection);
		model.put("count", collection.size());
		return "home";
	}


	@RequestMapping(value = "/image", method = RequestMethod.GET)
	public String image(Map<String, Object> model, 
			@RequestParam("name") String imageName) {
	    String user = getLoggedInUser();
		UserImageFileRepository store = 
				new UserImageFileRepository(user, config.getImageFilesRoot());
		model.put("hostname", config.getHostName());
		model.put("user", user);
		model.put("imagesWebPath", imagesWebPath);
		model.put("imagename", imageName);
		model.put("imageref", store.getWebPath(imageName));
		model.put("effects", new String[]{"Grayscale", "Invert", "Blur"});
		model.put("message", "");
		return "image";
	}
	

	@RequestMapping(value = "/image", method = RequestMethod.POST)
	public String imageSave(Map<String, Object> model, 
			@RequestParam(value="imagename", defaultValue="") String imageName, 
			@RequestParam(value="imagenew", defaultValue="") String imageNew) {
		String user = getLoggedInUser();
		String filesRoot = config.getImageFilesRoot();
		if (!imageNew.equals("")) {
			// We have a new image from processing, need to replace the original.
			UserImageFileRepository store = 
					new UserImageFileRepository(user, filesRoot);
			String destFilename = store.getPath(imageName);
			String srcFilename = filesRoot + '/' + processedFilesWebPath  + 
					'/' + imageNew.substring(imageNew.lastIndexOf('/') + 1);
			try {
				Files.move(Paths.get(URI.create(srcFilename)), 
						Paths.get(URI.create(destFilename)), 
						REPLACE_EXISTING );
			} catch (Exception e) {
				log.error(String.format("Failed to copy [%s] to [%s] (%s)", 
						srcFilename, destFilename, e.getMessage()));
			}			
		}
		return "redirect:/";
	}

		
	@RequestMapping(value = "/upload", method = RequestMethod.GET)
	public String upload(Map<String, Object> model) {
		return "upload";
	}

	@RequestMapping(value = "/upload", method = RequestMethod.POST)
	public String upload(Map<String, Object> model, 
			@RequestParam(value="imagename", defaultValue="") String imagename, 
			@RequestParam("uploadfile") MultipartFile uploadedfile) {
		String user = getLoggedInUser();
		UserImageFileRepository store = 
				new UserImageFileRepository(user, config.getImageFilesRoot());
		Boolean success = store.newUpload(imagename, uploadedfile);
		model.put("success", success);
		model.put("imagename", imagename);
		return "upload";
	}

	@RequestMapping("/logout")
	public String logout(Map<String, Object> model) {
		SecurityContextHolder.getContext()
			.getAuthentication().setAuthenticated(false);
		return "redirect:/";
	}



/*
 * REST methods for AJAX calls from client
 */

    /*
     * Because we are sending in a filename, we needed to add the 
     * regular expression to the end of the RequestMapping because Spring 
     * defaults to a regular expression of [^.]* (everything but a period)
     * However, because we a sending names of image/binary files the Spring 
     * component MappingJackson2HttpMessageConverter determines that the JSON 
     * content that we are sending back does not match the content requested 
     * and sends back a HTTP error code 406 back to the client.  To mitigate
     * this we need to define the configureContentNegotiation() method above.
     */
    @RequestMapping(value="/effectrequest/{name}/{imagename:[a-zA-Z0-9%\\.]*}", 
    		headers="Accept=*/*", method=RequestMethod.GET, 
    		produces = "application/json")
    public @ResponseBody EffectRequest effectRequest(
    		@PathVariable("name") String name,
    		@PathVariable("imagename") String imageName)    		
    {
	    String user = getLoggedInUser();
		String hostName = this.config.getHostName();
		UserImageFileRepository store = 
				new UserImageFileRepository(user, config.getImageFilesRoot());

		String status = "submitted";
        String correlationId = java.util.UUID.randomUUID().toString();


		Map<String, Object> mapObject = new HashMap<String, Object>();
		String imageFullPath = store.getPath(imageName);
		String ext = imageFullPath.substring(imageFullPath.lastIndexOf('.') + 1);
		String processedFileName = String.format("%s/%s/%s.%s", 
				config.getImageFilesRoot(),
				processedFilesWebPath, 
				correlationId, ext);
		
		long created = System.currentTimeMillis();
		mapObject.put("correlationId", correlationId);
		mapObject.put("user", user);
		mapObject.put("inputPath", imageFullPath);
		mapObject.put("ouputPath", processedFileName);
		mapObject.put("effectName", name);
		mapObject.put("requestHost", hostName);
		mapObject.put("requestCreated", created);

		JsonWrapper objJson = new JsonWrapper();
		String message = objJson.toJson(mapObject);

        BasicProperties props = new BasicProperties
                                    .Builder()
                                    .correlationId(correlationId)
                                    .replyTo(replyQueueName)
                                    .build();
        try {
			channel.basicPublish("", requestQueueName, props, message.getBytes());
			log.info(String.format("effectrequest\tsuccess\t%s\t%s\t%s", 
					hostName, user, correlationId));
		} catch (IOException e) {
			log.error(String.format("effectrequest\tfail\t%s\t%s\t%s\t%s", 
					hostName, user, correlationId, e.getMessage()));
			status = "failed";
		}
        return new EffectRequest(status, correlationId, created);
    }


    @RequestMapping(value="/effectfetch/{requestid}/{requestcreated}", 
    		headers="Accept=*/*", method=RequestMethod.GET, 
    		produces = "application/json")
    public @ResponseBody EffectFetch effectFetch(
    		@PathVariable("requestid") String correlationId,
    		@PathVariable("requestcreated") long requestCreated)
    {
	    String user = getLoggedInUser();
		String hostName = this.config.getHostName();
    	String status = "notready";
    	String url = "";
    	
    	String response = null;
    	QueueingConsumer.Delivery delivery = null;
    	try {
			delivery = consumer.nextDelivery(333);
	        if (delivery != null &&
	        	delivery.getProperties().getCorrelationId().equals(correlationId)) {
	            response = new String(delivery.getBody());
	    		JsonWrapper objJson = new JsonWrapper();
	    		Map<String, Object> mapObject = objJson.fromJson(response);
	            status = (String) mapObject.get("status");
	            String newFileName = (String) mapObject.get("ouputPath");
	            newFileName = 
	            		newFileName.substring(newFileName.lastIndexOf('/') + 1);
	    		url = "/" + imagesWebPath + "/" + 
	            		processedFilesWebPath + "/" + newFileName;

	    	    long curr = System.currentTimeMillis();
	    	    long start = (Long) mapObject.get("requestCreated");
	    	    long millisecondsElapsed = curr - start;    //Time difference in milliseconds
	    	    
	    		log.info(String.format("effectfetch\t%s\t%s\t%s\t%s\t%d", 
						status, hostName, user, correlationId, millisecondsElapsed));
	        } else {
	        	if ((System.currentTimeMillis() - requestCreated) > 30000) {
	    			status = "failed";
	    			log.error(String.format("effectrequest\tfail\t%s\t%s\t%s\t%s", 
	    					hostName, user, correlationId, "Max time exceeded"));	        		
	        	}
	        }
		} catch (ShutdownSignalException e) {
			status = "failed";
			log.error(String.format("effectrequest\tfail\t%s\t%s\t%s\t%s", 
					hostName, user, correlationId, e.getMessage()));			
		} catch (ConsumerCancelledException e) {
			status = "failed";
			log.error(String.format("effectrequest\tfail\t%s\t%s\t%s\t%s", 
					hostName, user, correlationId, e.getMessage()));			
		} catch (InterruptedException e) {
			status = "failed";
			log.error(String.format("effectrequest\tfail\t%s\t%s\t%s\t%s", 
					hostName, user, correlationId, e.getMessage()));			
		}
        return new EffectFetch(status, correlationId, url);
    }

	public static void main(String[] args) throws Exception {
		new SpringApplicationBuilder(WebApp.class, 
				"classpath:/META-INF/application-context.xml").run(args);
	}
 
	@Override
	public void addViewControllers(ViewControllerRegistry registry) {
		registry.addViewController("/login").setViewName("login");
	}
	
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler(imagesWebPathMask)
        	.addResourceLocations(config.getImageFilesRoot() + '/');
    }
    
	@Bean
	public ApplicationSecurity applicationSecurity() {
		return new ApplicationSecurity();
	}

	@Order(SecurityProperties.ACCESS_OVERRIDE_ORDER)
	protected static class ApplicationSecurity extends WebSecurityConfigurerAdapter {

		@Autowired
		private SecurityProperties security;

		@Autowired
		private DataSource dataSource;

		@Override
		protected void configure(HttpSecurity http) throws Exception {
			http
				.authorizeRequests()
				.antMatchers("/css/**", imagesWebPathMask).permitAll().anyRequest()
					.fullyAuthenticated().and().formLogin().loginPage("/login")
					.failureUrl("/login?error").permitAll();
		}

		@Override
		public void configure(AuthenticationManagerBuilder auth) throws Exception {
			auth.jdbcAuthentication().dataSource(this.dataSource);
		}

	}

}
