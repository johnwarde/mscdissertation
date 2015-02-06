package com.interop.webapp;

import org.springframework.stereotype.Component;

@Component
public class WebAppConfig {

	private String hostName;
	private String imageFilesRoot;
	private String queueHostName;
	private String queueName;

	public String getHostName() {
		return this.hostName;
	}
	public void setHostName(String value) {
		this.hostName = value;
	}
	
	public String getImageFilesRoot() {
		return imageFilesRoot;
	}
	public void setImageFilesRoot(String value) {
		this.imageFilesRoot = value;
	}

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
