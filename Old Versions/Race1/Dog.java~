import java.awt.image.*;
import java.io.*;
import javax.imageio.*;

public class Dog {
	
	private String name;
	private BufferedImage[] sprite;
	//use Sprite class with spritesheets
	private int spriteFrame;
	private int x_pos;
	private int y_pos;
	private int velocity;
	private int frameCounter;
	// frameCounter counts updates since last spriteFrame advance
	private int frameCounterMax = 3;
	// velocity < 6, make fewer updates/s
	// velocity > 10, make more updates/s, maybe not x_pos += 2/3 velocity
	
	public Dog(String name, int y_pos) {
	//replace with name and y_pos	
		this.name = name;
		sprite = new BufferedImage[4];
		try {
			for (int i = 0; i < 3; i++) {
				sprite[i] = ImageIO.read(new File("sprites/Shiba" + i + ".gif"));
			}
			sprite[3] = sprite[1];
		} catch (IOException ex) {
			ex.printStackTrace();
		}
		this.x_pos = 10;
		this.y_pos = y_pos;
		
		velocity = (int) Math.random()*3 + 6;
	}	
	
	public Dog() {
		this("dog", 7);
	}
	
	
	public int get_x() {
		return x_pos;
	}
	
	public int get_y() {
		return y_pos;
	}
	
	public BufferedImage getSprite() {
		return sprite[spriteFrame];
	}
	
	
	public void update() {
		
		//advance spriteFrame for every _frameCounterMax_ updates performed
		if (++frameCounter >= frameCounterMax) {
			spriteFrame = (spriteFrame + 1)%4;
			
			// get new velocity!! Then change advances/update if necessary
			velocity += (int) (Math.random()*3) - 1;
			if (velocity < 0) {
				velocity = 2;
			} else if (velocity > 14) {
				velocity = 13;
			}
			
			
			if (velocity < 6) {
				frameCounterMax = 5;
				x_pos += Math.ceil(velocity * (5.0 / 3.0));
			} else if (velocity > 9) {
				frameCounterMax = 2;
				x_pos += Math.ceil(velocity * (2.0 / 3.0));
			} else {
				frameCounterMax = 3;
				x_pos += velocity;
			}
			//test spriteFrame advancement
			System.out.println("Dog " + name + ": " + Integer.toString(velocity));
			//System.out.println("spriteFrame: " + spriteFrame);
			frameCounter = 0;
		}
		//test updates/frame
		//System.out.println("frameCounter: " + frameCounter);
		
		/* possible solution: figure out how many pixels to move per mvmt for natural animation
			try: velocity affects upper limit of frameCounter inversely
			higher velocity means moves forward more often
			Test first: try same mvmt distance for multiple frameCounts
			else work out relationship (move more often and move slightly less far?)
			A running sprite would probably be less awkward
			-> move more often and further, speed grades in sprites
		*/
		
		/* May want to transition to multithreading.
			New thread initialized as instance variable of each dog.
			Rather than tying frameCounterMax to velocity, have velocity affect update step time
			Copy loop method from gameLoop in main
			Main gameLoop will paint dogs at current pos/frame once each time it loops
			Instead of update method, have run method called at start of race? 
			Or have it start in own thread and block until race starts?
		*/
	}
	
	
}
