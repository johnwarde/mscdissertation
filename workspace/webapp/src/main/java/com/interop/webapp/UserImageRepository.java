/**
 * 
 */
package com.interop.webapp;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

/**
 * @author John Warde
 *
 */
public abstract class UserImageRepository {
	protected String owner;
	public UserImageRepository(String owner) {
		this.owner = owner;
	}
	
	abstract public List<ImageDetailBean> getWebPaths();
	abstract public String getWebPath(String imagename);
	abstract public String getPath(String imagename);
	abstract public Boolean newUpload(String imagename, MultipartFile file);
	abstract public void removeImage(String imagename);
}
