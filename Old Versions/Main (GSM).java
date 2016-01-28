import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.awt.geom.*;
import java.io.*;
import javax.imageio.*;
import javax.swing.*;
import java.util.*;

public class Main {
	private JFrame frame;
	private GamePanel panel;
	private MouseInput mouse;
	private BufferedImage bgImage;
	private Dog[] dogArray;
	private Dog selection;
	private long stepTime = (long)(1000/30);
	private GameState gameState;
	private boolean running;

	private Main() {
		frame = new JFrame();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		panel = new GamePanel();
		
		mouse = new MouseInput();
		panel.addMouseListener(mouse);
		panel.addMouseMotionListener(mouse);
		
		try {
		bgImage = ImageIO.read(new File("sprites/background.gif"));
		} catch (IOException ex) {
			ex.printStackTrace();
		}
		
		gameState = new SelectionState();
	
		frame.getContentPane().add(panel);		
		frame.setSize(650, 500);
		frame.setVisible(true);
	}

	
	public static void main(String[] args) {
		Main mainObject = new Main();
		
		mainObject.gameloop();	
	
	}

	/* private void setup() {
		dogArray = new Dog[4];
		for (int i = 0; i < 4; i++) {
			dogArray[i] = new Dog(Integer.toString(i+1), i);
			dogArray[i].set_y(i*100);
		}		
		
		gameState = new RaceState();
	}
	*/
	
	private void gameloop() {
		running = true;
		long startTime = System.currentTimeMillis();
		
		while(running) {
			//selection
			mouse.poll();
			// poll keyboard
			gameState.update();
			frame.repaint();
			if (System.currentTimeMillis() - startTime < stepTime) {
				try {
					Thread.sleep(stepTime - (System.currentTimeMillis() - startTime));		
				} catch (InterruptedException ex) {
					ex.printStackTrace();
				}
			}
			
			startTime = System.currentTimeMillis();
		}
		
		
	}
	
	
	private class SelectionState implements GameState {
		private final String stateName = "SELECTION";
		private int selected = -1;
		private RoundRectangle2D.Double[] boxes;
		private final double BOX_WIDTH = 560;
		private final double BOX_HEIGHT = 85;
		
		public SelectionState() {
			// initialize new array of competitors, start selection state		
			dogArray = new Dog[4];
			boxes = new RoundRectangle2D.Double[4];
			for (int i = 0; i < 4; i++) {
				// initialize new dogArray
				dogArray[i] = new Dog(Integer.toString(i+1), i);
				
				// initialize boxes for drawing
				double rect_y = 40 + (105 * i);
				boxes[i] = new RoundRectangle2D.Double(40, rect_y, BOX_WIDTH, BOX_HEIGHT, 5, 5);
			}
			//RoundRectangle2D.Double(double x, double y, BOX_WIDTH, BOX_HEIGHT, double arcw, double arch)
		}
		
		public String get_name() {
			return stateName;
		}
		
		public void update() {
			
			for (Dog d: dogArray) {
				d.update();
			}
			
			// handle input for keyboard (selection based on up-down)
			
			Point mousePos = mouse.getPosition();
			for (int i = 0; i < boxes.length; i++) {
				if (boxes[i].contains(mousePos)) {
					selected = i;
				}
			}
			
			boolean choice = false;
			if (mouse.buttonClicked(1)) {
				if (boxes[selected].contains(mousePos)) {
					choice = true;
				}
			}
			/* else if (//enter pressed && selected != null) {
				choice = true;
			} */
			// if valid click or enter press:
			if (choice) {
				System.out.println("Selected dog " + selected + "!");
			// set this.selected to game-wide selection
			// call advance-gamestate method
			}
			return;
		}
		
		public void paint(Graphics2D g2d) {
			g2d.setColor(new Color(0xCCCCCC));
			g2d.fillRect(0, 0, panel.getWidth(), panel.getHeight());
			g2d.setColor(new Color(0xFFFFFF));
			if (selected != -1) {
				// outline rectangle at boxes[selected]
				RoundRectangle2D.Double outline = new RoundRectangle2D.Double(boxes[selected].getX() - 6, boxes[selected].getY() - 6, BOX_WIDTH + 12, BOX_HEIGHT + 12, 10, 10);
				g2d.fill(outline);
			}	
			g2d.setColor(new Color(0x00CCFF));
			for (int i = 0; i < 4; i++) {
				g2d.fill(boxes[i]);
				int dog_x = (int)boxes[i].getX() + 15;
				int dog_y = (int)boxes[i].getY() + 6;
				g2d.drawImage(dogArray[i].getSprite(gameState.get_name()), dog_x, dog_y, null);
				// drawString(dogArray[i].getName() ...
			}
			return;
		}

	}
	
	
	private class RaceState implements GameState {
		public final String stateName = "RACE";
		private int winners = 0;

		public String get_name() {
			return stateName;
		}
		
		public void update() {
							
			for(Dog d: dogArray) {
				d.race();
			}
			
			if (winners > 2) {
				for (Dog d: dogArray) {
					if (d.getRanking = -1) d.setRanking(++winners);
				}
				advanceState();
				// advances state the cycle after crossing the finish line is painted
			} else {
				for (Dog d: dogArray) {
					if (d.get_x() >= 1100 && d.getRanking() != -1) {
						d.setRanking(++winners)
					}
				}
			}

		}
		
		public void paint(Graphics2D g2d) {
			int viewPos = 0;
			
			int panelWidth = panel.getWidth();
			int averagePos = getAveragePos(dogArray);
			// get average x_pos of dogs in array to figure out optimum viewPos
			
			if (averagePos < (0.5 * panelWidth)) {
				// average in first half-panel of background
				viewPos = 0;
			} else if (averagePos > (bgImage.getWidth() - (panelWidth * 0.5))) {
				// average in last half-panel of background
				viewPos = bgImage.getWidth() - panelWidth;
			} else {
				viewPos = averagePos - (int)(0.5 * panelWidth);
			}
			
			//draw bg at viewPos, dog at x_pos - viewPos
			g2d.drawImage(bgImage, 0, 0, panel.getWidth(), panel.getHeight(), viewPos, 0, viewPos + panel.getWidth(), panel.getHeight(),  null);
			// arguments: drawImage(Image img, int dstx1, int dsty1, int dstx2, int dsty2, int srcx1, int srcy1, int srcx2, int srcy2, ImageObserver observer)
			
			for (Dog d: dogArray) {
				g2d.drawImage(d.getSprite(gameState.get_name()), d.get_x() - viewPos, d.get_y(), null);
			}
		}
		
		private int getAveragePos(Dog[] dogArray) {
			double sum = 0.0;
			for (Dog d: dogArray) {
				sum += d.get_x();
			}
			return ((int) Math.floor(sum / dogArray.length));
		}
	}

	private class TitleState implements GameState {
		public final String stateName = "TITLE";
		
		public String get_name() {
			return stateName;
		}
		
		public void update() {
			return;
		}
		
		public void paint(Graphics2D g2d) {
			g2d.setColor(new Color(0xCCCCCC));
			g2d.fillRect(0, 0, panel.getWidth(), panel.getHeight());
			return;
		}
	}
	
	/* Made a GSM class because wanted gamestate to change at the END of a gameloop cycle
		Worried about potential memory leak:
		If gamestate update method calls for change directly, the update method it is in
		(and therefore the current gamestate object) will stay in memory because call executes
		in current method
		!! Disproven - update method completes before gameloop continues
		I thought it would execute the new gamestate's update method from within?
		May still want to use GSM ... new gamestate will completely instantiate before 
		update and subsequent paint complete
		!! Paint will be called before first update of state if no GSM
	*/
	private class GameStateManager() {
		private boolean advance = false;
	
		public void advanceState() {
			advance = true;
		}
		
		public void update() {
			if (advance) {
				switch (gameState.get_name()) {
					case "TITLE":
						gameState = new SelectionState();
						break;
					case "SELECTION": 
						gameState = new RaceState();
						break;
					case "RACE": 
						//gameState = new ResultsState();
						break;
					case "RESULTS": 
						gameState = new SelectionState();
						break;
					default: 
						System.out.println("GameState not initialized.");
				}
				advance = false;
			}
		}
	}


	private class GamePanel extends JPanel {
		
		@Override
		public void paintComponent(Graphics g) {
			super.paintComponent(g);
			Graphics2D g2d = (Graphics2D) g;
			gameState.paint(g2d);
		}
		
	}
			
				
			
}


