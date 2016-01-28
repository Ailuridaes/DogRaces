import java.awt.image.*;

public class Dog {
	
	private String name;
	private Spritesheet spritesheet;
	private int spriteFrame;
	private int x_pos;
	private int y_pos;
	private int velocity;
	private int ranking = -1;
	private int frameCounter;
	// frameCounter counts updates since last spriteFrame advance
	private int frameCounterMax = 6;
	// velocity < 6, make fewer updates/s
	// velocity > 10, make more updates/s, maybe not x_pos += 2/3 velocity
	
	public Dog(String name, int pos) {	
		this.name = name;
		this.spritesheet = new Spritesheet(name);
		this.x_pos = 10;
		this.y_pos = (pos*100) + 15;
		
		velocity = (int) Math.random()*3 + 6;
	}	
	
	public Dog(int pos) {
		switch (pos) {
			case 0: this.name = "Fido";
				break;
			case 1: this.name = "Rufus";
				break;
			case 2: this.name = "Spot";
				break;
			case 3: this.name = "Riley";
				break;
			default: this.name = "Dog";
		}
		
		this.spritesheet = new Spritesheet();
		this.x_pos = 10;
		this.y_pos = (pos*100) + 15;
		
		velocity = (int) Math.random()*3 + 6;
	}
	
	public Dog() {
		this("dog", 0);
	}
	
	public String getName() {
		return name;
	}
	
	public int get_x() {
		return x_pos;
	}
	
	public int get_y() {
		return y_pos;
	}
	
	public void set_y(int y) {
		y_pos = y;
	}
	
	public int getRanking() {
		return ranking;
	}
	
	public void setRanking(int r) {
		ranking = r;
	}
		
	
	public BufferedImage getSprite(String gamePhase) {
		int row = 0;
        switch (gamePhase) {
            case "SELECTION":
                row = 0;
                break;
            case "RACE":
                row = 1;
                break;
            case "RESULTS":
                row = 0;
                break;
            default:
                row = 3;
        }
		return spritesheet.getSprite(spriteFrame, row);
	}
	
	public void update() {
		if (++frameCounter >= frameCounterMax) {
			spriteFrame = (spriteFrame + 1)%4;
			frameCounter = 0;
		}
	}
	
	public void race() {
		
		//advance spriteFrame for every _frameCounterMax_ updates performed
		if (++frameCounter >= frameCounterMax) {
			spriteFrame = (spriteFrame + 1)%4;
			
			// get new velocity!! Then change advances/update if necessary
			velocity += (int) (Math.random()*3) - 1;
			if (velocity < 5) {
				velocity = 6;
			} else if (velocity > 15) {
				velocity = 14;
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
			//System.out.println("Dog " + name + ": " + Integer.toString(velocity));
			//System.out.println("spriteFrame: " + spriteFrame);
			frameCounter = 0;
		}
		//test updates/frame
		//System.out.println("frameCounter: " + frameCounter);
		
		/* possible solution: figure out how many pixels to move per mvmt for natural animation
			velocity affects upper limit of frameCounter inversely
			higher velocity means moves forward more often
			-> move more often and further, speed grades in sprites
		*/
		
		/* May want to implement multithreading.
			New thread initialized as instance variable of each dog.
			Rather than tying frameCounterMax to velocity, have velocity affect update step time
			Copy loop method from gameLoop in main
			Main gameLoop will paint dogs at current pos/frame once each time it loops
			Instead of update method, have run method called at start of race? 
			Or have it start in own thread and block until race starts?
		*/
	}
	
	
}
