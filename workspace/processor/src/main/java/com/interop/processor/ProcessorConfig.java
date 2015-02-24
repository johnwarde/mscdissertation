/**
 * 
 */
package com.interop.processor;

import org.springframework.stereotype.Component;

/**
 * @author johnwarde
 *
 */
@Component
public class ProcessorConfig {

	private String queueHostName;
	private String queueName;

	public String getQueueHostName() {
		return this.queueHostName;
	}
	public void setQueueHostName(String value) {
		this.queueHostName = value;
	}
	
	public String getQueueName() {
		return this.queueName;
	}
	public void setQueueName(String value) {
		this.queueName = value;
	}
	
}
