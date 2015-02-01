/**
 * 
 */
package com.interop.webapp;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author johnwarde
 *
 */
public final class UserImageFileRepository extends UserImageRepository {
	// Root of the file repository.
	private String root;

	static Logger log = Logger.getLogger(WebApp.class.getName());
	
	/**
	 * @param owner
	 */
	public UserImageFileRepository(String owner, String repositoryRoot) {
		super(owner);
		this.root = repositoryRoot;
	}

	/* (non-Javadoc)
	 * @see com.interop.webapp.UserImageRepository#getWebPaths()
	 */
	@Override
	public List<ImageDetailBean> getWebPaths() {
		ensureOwnerFolder();
		String absolutePath;
		List<ImageDetailBean> collection = new ArrayList<ImageDetailBean>();
		File folder = new File(URI.create(getUserFolder()));
		File[] listOfFiles = folder.listFiles(); 
		for (int i = 0; i < listOfFiles.length; i++) 
		{ 
		   if (listOfFiles[i].isFile()) 
		   {
			   ImageDetailBean img = new ImageDetailBean();
			   img.setImageName(listOfFiles[i].getName());
			   absolutePath = (String) listOfFiles[i].toURI().toString().replace("C:/", "");
			   img.setRelativePath(absolutePath.substring(root.length()));
			   img.setAbsolutePath(absolutePath);
			   collection.add(img);  
		   }
		}
		return collection;
	}

	/* (non-Javadoc)
	 * @see com.interop.webapp.UserImageRepository#getWebPath(java.lang.String)
	 */
	@Override
	public String getWebPath(String imagename) {
		return owner + '/' + imagename;
	}

	/* (non-Javadoc)
	 * @see com.interop.webapp.UserImageRepository#getPath(java.lang.String)
	 */
	@Override
	public String getPath(String imagename) {
		return getUserFolder() + '/' + imagename;
	}

	/* (non-Javadoc)
	 * @see com.interop.webapp.UserImageRepository#newUpload(java.lang.String, org.springframework.web.multipart.MultipartFile)
	 */
	@Override
	public Boolean newUpload(String imagename, MultipartFile uploadedfile) {
		ensureOwnerFolder();
		if (imagename.trim().equals("")) {
			imagename = uploadedfile.getOriginalFilename();
		}
		String filename = getPath(imagename);
		File imageFile = new File(URI.create(filename));
		if (imageFile.exists()) {
			return false;
		}
	    System.out.println("moving uploaded image file to : " + filename);
	    log.info("moving uploaded image file to : " + filename);	    
    	try {
			FileOutputStream fos = new FileOutputStream(URI.create(filename).getRawPath());
			fos.write(uploadedfile.getBytes());
			fos.close();
		} catch (IllegalStateException e) {
		    System.out.println(String.format("ERROR: moving uploaded image file: %s (%s)", filename, e.getMessage()));		    	
		} catch (IOException e) {
		    System.out.println(String.format("ERROR: moving uploaded image file: %s (%s)", filename, e.getMessage()));
		    log.error(String.format("moving uploaded image file: %s (%s)", filename, e.getMessage()));
		}
		return true;
	}

	/* (non-Javadoc)
	 * @see com.interop.webapp.UserImageRepository#removeImage(java.lang.String)
	 */
	@Override
	public void removeImage(String imagename) {
		String filename = getPath(imagename);
		File imageFile = new File(URI.create(filename));
		if (!imageFile.exists()) {
			return;
		}

	    System.out.println("deleting image file: " + filename);	
	    try {
	    	imageFile.delete();
	    } catch (SecurityException se) {
		    System.out.println(String.format("ERROR: deleting image file: %s (%s)", filename, se.getMessage()));		    	
	    }			
	}

	private String getUserFolder() {
		return root + '/' + owner;
	}
	
	private void ensureOwnerFolder() {
		String userFolder = getUserFolder();
		File ownerFolder = new File(URI.create(userFolder));
		if (ownerFolder.exists()) {
			return;
		}

	    System.out.println("creating directory: " + userFolder);	
	    try {
	    	ownerFolder.mkdir();
	    } 
	    catch (SecurityException se) {
		    System.out.println(String.format("ERROR: creating directory: %s (%s)", userFolder, se.getMessage()));		    	
	    }	
	    catch (Exception e) {
		    System.out.println(String.format("ERROR: creating directory: %s (%s)", userFolder, e.getMessage()));		    	
	    }	
	}
}
