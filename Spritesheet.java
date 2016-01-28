import java.awt.*;
import java.awt.image.*;
import java.io.*;
import javax.imageio.*;
import java.util.*;

public class Spritesheet {

    private BufferedImage spritesheet;
    private static final int TILE_SIZE = 72;

    public Spritesheet(String name) {
		File folder = new File("res/dogSprites");
		if (folder.isDirectory()) {
			try {
				spritesheet = ImageIO.read(new File("res/dogSprites/" + name + ".gif"));
				return;
			} catch (IOException e) {
				e.printStackTrace();
				
			}
		}
		// executes only if exception occurred or if folder.isDirectory() = false
		spritesheet = (BufferedImage) Toolkit.getDefaultToolkit().getImage(getClass().getResource("res/Shiba_sprites.gif"));
    }    
    
    public Spritesheet() {  	
        System.out.println("ran default constructor");
		
		//spritesheet = (BufferedImage) Toolkit.getDefaultToolkit().getImage(getClass().getResource("res/Shiba_sprites.gif")).getBufferedImage();
		try {
			spritesheet = ImageIO.read(getClass().getResource("res/Shiba_sprites.gif"));
		} catch (IOException e) {
            e.printStackTrace();
        }
		
		
		/*
		try {
            spritesheet = ImageIO.read(new File("res/Shiba_sprites.gif"));
        } catch (IOException e) {
            e.printStackTrace();
        }
		*/
    } 
    
    

    public BufferedImage getSprite(int xGrid, int yGrid) {
        return spritesheet.getSubimage(xGrid * TILE_SIZE, yGrid * TILE_SIZE, TILE_SIZE, TILE_SIZE);
    }

}
