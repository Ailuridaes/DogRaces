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
	long stepTime = (long)(1000/30);
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
				g2d.drawImage(d.getSprite(), d.get_x() - viewPos, d.get_y(), null);
			}
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


