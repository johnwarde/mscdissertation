package com.interop.processor;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.awt.image.ConvolveOp;
import java.awt.image.Kernel;
import java.io.File;
import java.net.URI;

import javax.imageio.ImageIO;

import org.apache.log4j.Logger;

public class EffectsApplicator {
	private enum Effect {Grayscale, Invert, Blur}
	static Logger log = Logger.getLogger(ProcessorApp.class.getName());
	
	public String apply(String effectName, String inputPath,
			String ouputPath) {
		BufferedImage image = null;
    	Boolean success = true;
	    try {
	    	File input = new File(URI.create(inputPath));
	    	image = ImageIO.read(input);
		}
	    catch (Exception e) {
	    	log.error(String.format("Failed to read input image file (%s) - %s", 
	    			inputPath, e.getMessage()));
	    	success = false;
	    }
	    if (!success) {
		    return "failed";
	    }
	    
    	switch (Effect.valueOf(effectName)) {
		case Grayscale:
			success = grayscale(image);
			break;
		case Invert:
			success = invert(image);
			break;
		case Blur:
			image = blur(image);
			break;
		default:
			break;
		}

    	try {
	    	File ouptut = new File(URI.create(ouputPath));
	    	String ext = ouputPath.substring(ouputPath.lastIndexOf('.') + 1)
	    			.toLowerCase();
	    	ImageIO.write(image, ext, ouptut);
			}
	    catch (Exception e) {
	    	log.error(String.format("Failed to write to image file (%s) - %s", 
	    			ouputPath, e.getMessage()));
	    	success = false;
	    }
	    if (success) {
			return "completed";
		}
	    return "failed";
	}
	
	
	private Boolean grayscale(BufferedImage image) {
		int width;
		int height;
		width = image.getWidth();
		height = image.getHeight(); 
		for(int i=0; i<height; i++){ 
			for(int j=0; j<width; j++){
			   Color c = new Color(image.getRGB(j, i));
			   int red = (int)(c.getRed() * 0.299);
			   int green = (int)(c.getGreen() * 0.587);
			   int blue = (int)(c.getBlue() *0.114);
			   int rgbAdded = red+green+blue;
			   Color newColor = new Color(rgbAdded, rgbAdded,rgbAdded);
			   image.setRGB(j,i,newColor.getRGB());
			}
		}		
		return true;
	}
	

	private Boolean invert(BufferedImage image) {
        for (int x = 0; x < image.getWidth(); x++) {
            for (int y = 0; y < image.getHeight(); y++) {
                int rgba = image.getRGB(x, y);
                Color col = new Color(rgba, true);
                col = new Color(255 - col.getRed(),
                                255 - col.getGreen(),
                                255 - col.getBlue());
                image.setRGB(x, y, col.getRGB());
            }
        }
		return true;
	}

	
	private BufferedImage blur(BufferedImage image) {
		float ninth = 1.0f / 9.0f;
		float[] blurKernel = {
		    ninth, ninth, ninth,
		    ninth, ninth, ninth,
		    ninth, ninth, ninth
		};
		BufferedImageOp blur = new ConvolveOp(new Kernel(3, 3, blurKernel));
		BufferedImage alteredImage = blur.filter(image, null);
		return alteredImage;
	}
	
}
