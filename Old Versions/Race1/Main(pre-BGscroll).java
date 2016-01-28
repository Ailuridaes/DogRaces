import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.io.*;
import javax.imageio.*;
import javax.swing.*;
import java.util.*;

public class Main {
	private JFrame frame;
	private GamePanel panel;
	private BufferedImage bgImage;
	private Dog[] dogArray;
	long stepTime = (1000/30);
	boolean running;

	private Main() {
		frame = new JFrame();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		panel = new GamePanel();
		
		try {
		bgImage = ImageIO.read(new File("sprites/background.gif"));
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	
		frame.getContentPane().add(panel);		
		frame.setSize(640, 480);
		frame.setVisible(true);
}

	
	public static void main(String[] args) {
		Main mainObject = new Main();
		
		mainObject.test();
		
		mainObject.gameloop();
		
	
	
	}

	private void test() {
		dogArray = new Dog[4];
		for (int i = 0; i < 4; i++) {
			dogArray[i] = new Dog(Integer.toString(i+1), 100*i);
		}		
	}
	
	private void gameloop() {
		long startTime = System.currentTimeMillis();
		running = true;
		
		while(running) {
			
			for(Dog d: dogArray) {
				d.update();
			}
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

	private class GamePanel extends JPanel {
	
		public void paintComponent(Graphics g) {
			Graphics2D g2d = (Graphics2D) g;
			g2d.drawImage(bgImage, 0, 0, null);
			for (Dog d: dogArray) {
				g2d.drawImage(d.getSprite(), d.get_x(), d.get_y(), null);
			}
		}
		
	}
			
			
			
			
}


