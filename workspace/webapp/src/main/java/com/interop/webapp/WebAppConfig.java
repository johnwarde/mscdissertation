package com.interop.webapp;

import org.springframework.stereotype.Component;

@Component
public class WebAppConfig {

	private String hostName;
	private String imageFilesRoot;

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

}
