package io.github.libgdx.cubegame2.utils;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.imageio.ImageIO;

import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;

public class Textures {
	public static Texture createTexture(java.awt.Color c, boolean withBorder) {
		BufferedImage bufferedImage = new BufferedImage(80, 80, BufferedImage.TYPE_INT_ARGB);
	    Graphics2D g2d = bufferedImage.createGraphics();
	    
	    if (withBorder) {
		    g2d.setColor(java.awt.Color.BLACK);
		    g2d.fillRect(0, 0, 80, 80);
		    
		    g2d.setColor(c);
		    g2d.fillRect(1, 1, 78, 78);
	    } else {
	    	g2d.setColor(c);
	    	g2d.fillRect(0, 0, 80, 80);
	    }
	    
	    ByteArrayOutputStream baos = new ByteArrayOutputStream();
	    try {
			ImageIO.write(bufferedImage, "png", baos);
		} catch (IOException e) {
			e.printStackTrace();
		}
	    byte[] byteArray = baos.toByteArray();
	    Pixmap mask = new Pixmap(byteArray, 0, byteArray.length);
		
	    return new Texture(mask);
	}
}
