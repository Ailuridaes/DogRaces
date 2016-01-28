import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.awt.geom.*;
import java.io.*;
import javax.imageio.*;
import java.net.URL;
import javax.swing.*;
import java.util.*;

public class Main {
	private JFrame frame;
	private GamePanel panel;
	private MouseInput mouse;
	private KeyboardInput keyboard;
	private BufferedImage bgImage;
	private Dog[] dogArray;
	private Dog selection;
	private long stepTime = (long)(1000/30);
	private GameState gameState;
	private boolean running;
	private long cash;

	private Main() {
		frame = new JFrame();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		panel = new GamePanel();
		
		mouse = new MouseInput();
		keyboard = new KeyboardInput();
		panel.addMouseListener(mouse);
		panel.addMouseMotionListener(mouse);
		panel.addKeyListener(keyboard);
		panel.setFocusable(true);
		panel.requestFocusInWindow();
		
		try {
		URL bgURL = getClass().getResource("res/background.gif");
		bgImage = ImageIO.read(bgURL);
		// bgImage = ImageIO.read(new File("sprites/background.gif"));
		} catch (IOException ex) {
			ex.printStackTrace();
		}
		
		//TODO: load cash from save file; write to save file after each race/state
		//can put write to save file in advanceState() method
		cash = 500;
		gameState = new SelectionState();
	
		frame.getContentPane().add(panel);		
		frame.setSize(650, 500);
		frame.setVisible(true);
	}

	
	public static void main(String[] args) {
		Main mainObject = new Main();
		
		mainObject.gameloop();	
	}

	
	private void gameloop() {
		running = true;
		long startTime = System.currentTimeMillis();
		
		while(running) {
			//selection
			mouse.poll();
			keyboard.poll();
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
			selection = null;
			
			// initialize new array of competitors, start selection state
			dogArray = new Dog[4];
			boxes = new RoundRectangle2D.Double[4];
			
			File spriteFolder = new File("res/dogSprites");
			ArrayList<String> dogNames;
			if (spriteFolder.exists()) {
				dogNames = new ArrayList<String>(Arrays.asList(spriteFolder.list()));
			} else {
				dogNames = new ArrayList<String>();
			}
			
			for (int i = 0; i < 4; i++) {
				// initialize new dogArray
				boolean nameTaken;
				// TODO: need escape in case all names taken/not finding >3 files?
				// check before loop, missing resources error? Fill index with same name.
				/*
					Solution - Allow customization by pulling from folder by default
					If not enough competitors found, use default sprite resource IN .jar
					and give default names (Switch i: case 1 = Fido, Rufus, etc.)
				*/
				
				do {
						nameTaken = false;
					if(!dogNames.isEmpty()){
						int indx = (int)(Math.random() * (dogNames.size()));
						if (dogNames.get(indx).contains(".gif")) {
							dogArray[i] = new Dog(dogNames.remove(indx).replace(".gif",""), i);
						} else {
							dogNames.remove(indx);
							nameTaken = true;
						}
					} else{
						dogArray[i] = new Dog(i);						
					}
				} while (nameTaken); 
				
				// initialize boxes for drawing
				double rect_y = 40 + (105 * i);
				boxes[i] = new RoundRectangle2D.Double(40, rect_y, BOX_WIDTH, BOX_HEIGHT, 10, 10);
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
			if (keyboard.keyDownOnce(KeyEvent.VK_DOWN)) {
				selected = (selected + 1)%4;
			}
			if (keyboard.keyDownOnce(KeyEvent.VK_UP)) {
				selected = (selected + 3)%4;
			}
			
			// handle mouse input
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
			} else if (keyboard.keyDownOnce(KeyEvent.VK_ENTER) && selected != -1) {
				choice = true;
			}
			
			// if valid click or enter press:
			if (choice) {
				selection = dogArray[selected];
				System.out.println("Selected " + selection.getName() + "!");
				// maybe animate by flashing selection border for several frames
				cash -= 10;
				advanceState();
			}
			return;
		}
		
		public void paint(Graphics2D g2d) {
			g2d.setColor(new Color(0xA0A0A0));
			g2d.fillRect(0, 0, panel.getWidth(), panel.getHeight());
			g2d.setColor(new Color(0xD6BE99));
			g2d.setFont(new Font("SanSerif", Font.BOLD, 64));
			if (selected != -1) {
				// outline rectangle at boxes[selected]
				RoundRectangle2D.Double outline = new RoundRectangle2D.Double(boxes[selected].getX() - 6, boxes[selected].getY() - 6, BOX_WIDTH + 12, BOX_HEIGHT + 12, 15, 15);
				g2d.fill(outline);
			}	
			
			for (int i = 0; i < 4; i++) {
				g2d.setColor(new Color(0x2D4D70));
				g2d.fill(boxes[i]);
				int dog_x = (int)boxes[i].getX() + 15;
				int dog_y = (int)boxes[i].getY() + 6;
				g2d.drawImage(dogArray[i].getSprite(gameState.get_name()), dog_x, dog_y, null);
				g2d.setColor(new Color(0xF5F5F5));
				g2d.drawString(dogArray[i].getName(), dog_x + 144, dog_y + 60); 
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
					if (d.getRanking() == -1) d.setRanking(++winners);
				}
				advanceState();
				// advances state the cycle after crossing the finish line is painted
			} else {
				for (Dog d: dogArray) {
					if (d.get_x() >= 1100 && d.getRanking() == -1) {
						d.setRanking(++winners);
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
			g2d.drawImage(bgImage, 0, 0, panelWidth, panel.getHeight(), viewPos, 0, viewPos + panelWidth, panel.getHeight(),  null);
			// arguments: drawImage(Image img, int dstx1, int dsty1, int dstx2, int dsty2, int srcx1, int srcy1, int srcx2, int srcy2, ImageObserver observer)
			
			for (Dog d: dogArray) {
				g2d.drawImage(d.getSprite(gameState.get_name()), d.get_x() - viewPos, d.get_y(), null);
			}
			
			// draw cash amount in upper right
			g2d.setFont(new Font("SanSerif", Font.BOLD, 42));
			g2d.setColor(new Color(0xF5F5F5));
			Rectangle2D cashBounds = g2d.getFontMetrics().getStringBounds('$' + Long.toString(cash), g2d);
			g2d.drawString('$' + Long.toString(cash), panelWidth - (int)cashBounds.getWidth() - 10, (int)cashBounds.getHeight() + 10); 
				
		}
		
		private int getAveragePos(Dog[] dogArray) {
			double sum = 0.0;
			for (Dog d: dogArray) {
				sum += d.get_x();
			}
			return ((int) Math.floor(sum / dogArray.length));
		}
	}

	private class ResultsState implements GameState {
		public final String stateName = "RESULTS";
		
		public ResultsState() {
			String selectionRank = Integer.toString(selection.getRanking());
			switch(selectionRank) {
				case "1":
					cash += 25;
					selectionRank += "st";
					break;
				case "2":
					cash += 15;
					selectionRank += "nd";
					break;
				case "3":
					cash += 5;
					selectionRank += "rd";
					break;
				case "4":
					cash += 0;
					selectionRank += "th";
					break;
				default:
					selectionRank += "sth";
			}
			System.out.println("Your dog came in " + selectionRank + " place!");
			rankDogs();
			System.out.println("Rankings:");
			System.out.println("First place: " + dogArray[0].getName());
			System.out.println("Second place: " + dogArray[1].getName());
			System.out.println("Third place: " + dogArray[2].getName());
			System.out.println("Fourth place: " + dogArray[3].getName());
		}
		
		public String get_name() {
			return stateName;
		}
		
		public void update() {
			
			if (mouse.buttonClicked(1)||keyboard.keyDownOnce(KeyEvent.VK_ENTER)) {
				advanceState();
			}
			dogArray[0].update();
		}
		
		public void paint(Graphics2D g2d) {
			g2d.setColor(new Color(0xA0A0A0));
			g2d.fillRect(0, 0, panel.getWidth(), panel.getHeight());
			
			BufferedImage winnerSprite = dogArray[0].getSprite(gameState.get_name());
			g2d.drawImage(winnerSprite, panel.getWidth()/2 - winnerSprite.getWidth()/2, panel.getHeight()/2 - winnerSprite.getHeight()/2, null);
			
			// draw cash amount in upper right
			g2d.setFont(new Font("SanSerif", Font.BOLD, 42));
			g2d.setColor(new Color(0xF5F5F5));
			Rectangle2D cashBounds = g2d.getFontMetrics().getStringBounds('$' + Long.toString(cash), g2d);
			g2d.drawString('$' + Long.toString(cash), panel.getWidth() - (int)cashBounds.getWidth() - 10, (int)cashBounds.getHeight() + 10); 
		}
	}
	
	private class TitleState implements GameState {
		public final String stateName = "TITLE";
		
		public String get_name() {
			return stateName;
		}
		
		public void update() {
			if (mouse.buttonClicked(1)||keyboard.keyDownOnce(KeyEvent.VK_ENTER)) {
				advanceState();
			}
			return;
		}
		
		public void paint(Graphics2D g2d) {
			g2d.setColor(new Color(0xA0A0A0));
			g2d.fillRect(0, 0, panel.getWidth(), panel.getHeight());
			return;
		}
	}
	

	public void advanceState() {
		switch (gameState.get_name()) {
			case "TITLE":
				gameState = new SelectionState();
				break;
			case "SELECTION": 
				gameState = new RaceState();
				break;
			case "RACE": 
				gameState = new ResultsState();
				break;
			case "RESULTS": 
				gameState = new SelectionState();
				break;
			default: 
				System.out.println("GameState not initialized.");
				gameState = new SelectionState();
		}
	}

	public void rankDogs() {
		// insertion sort
		Dog temp;
        for (int i = 1; i < dogArray.length; i++) {
            for(int j = i ; j > 0 ; j--){
                if(dogArray[j].getRanking() < dogArray[j-1].getRanking()){
                    temp = dogArray[j];
                    dogArray[j] = dogArray[j-1];
                    dogArray[j-1] = temp;
                } else {
					break;
				}
            }
        }
		temp = null;
        return;
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

/* 
	// center text in THIS component - to use in paint method of panel
	Graphics2D g2d = (Graphics2D) g;
	FontMetrics fm = g2d.getFontMetrics();
	Rectangle2D r = fm.getStringBounds(stringTime, g2d);
	int x = (this.getWidth() - (int) r.getWidth()) / 2;
	int y = (this.getHeight() - (int) r.getHeight()) / 2 + fm.getAscent();
	g.drawString(stringTime, x, y);
*/
