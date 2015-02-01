/**
 * 
 */
package com.interop.webapp;

/**
 * @author johnwarde
 *
 */
public class ImageDetailBean implements java.io.Serializable {
	 
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
     * Property <code>imageName</code>
     */
    private String imageName = null;
 
    /**
     * Property <code>relativePath</code>
     */
    private String relativePath = null;

    /**
     * Property <code>absolutePath</code>
     */
    private String absolutePath = null;
 
    public ImageDetailBean() {
    }
 
    
    /**
     * Getter for property <code>imageName</code>
     */
    public String getImageName() {
        return imageName;
    }
 
    /**
     * Setter for property <code>imageName</code>.
     * @param value
     */
    public void setImageName(final String value) {
    	imageName = value;
    }    
    
    
   
    
    /**
     * Getter for property <code>relativePath</code>
     */
    public String getRelativePath() {
        return relativePath;
    }
 
    /**
     * Setter for property <code>name</code>.
     * @param value
     */
    public void setRelativePath(final String value) {
    	relativePath = value;
    }    
    
    
    /**
     * Getter for property <code>absolutePath</code>
     */
    public String getAbsolutePath() {
        return absolutePath;
    }
 
    /**
     * Setter for property <code>absolutePath</code>.
     * @param value
     */
    public void setAbsolutePath(final String value) {
    	absolutePath = value;
    }

 
    
}

