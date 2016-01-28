/*
	Modified from code by Tim Wright
	in Java Games: Keyboard and Mouse tutorial
	http://www.gamedev.net/page/resources/_/technical/general-programming/java-games-keyboard-and-mouse-r2439
*/

import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;


public class MouseInput implements MouseListener, MouseMotionListener {

  private static final int BUTTON_COUNT = 3;
  // Polled position of the mouse cursor
  private Point mousePos = null;
  // Current position of the mouse cursor
  private Point currentPos = null;
  // Current state of mouse buttons (down = true, up = false)
  private boolean[] state = null;
  // Polled mouse buttons
  private MouseState[] poll = null;
        
  private enum MouseState {
    RELEASED, // Not down
    PRESSED,  // Down
    CLICKED   // Released since last poll; change to pressed since last poll if want immediate response
  }


  public MouseInput() {
    // Create default mouse positions
    mousePos = new Point( 0, 0 );
    currentPos = new Point( 0, 0 );
    // Setup initial button states
    state = new boolean[ BUTTON_COUNT ];
    poll = new MouseState[ BUTTON_COUNT ];

    for( int i = 0; i < BUTTON_COUNT; ++i ) {
      poll[ i ] = MouseState.RELEASED;
    }
  }



  public synchronized void poll() {
    // Save the current location
    mousePos = new Point( currentPos );
    
	// Check each mouse button
    for( int i = 0; i < BUTTON_COUNT; ++i ) {
      // If the button is up for the first
      // time, it is CLICKED, otherwise it is
      // PRESSED.  

		if( !state[ i ] ) {
			if( poll[ i ] == MouseState.PRESSED )
				poll[ i ] = MouseState.CLICKED;
			else
				poll[ i ] = MouseState.RELEASED;
		} else {
			// button is down
			poll[ i ] = MouseState.PRESSED;
		}
    }
  }



  public Point getPosition() {
    return mousePos;
  }


  public boolean buttonClicked( int button ) {
    return poll[ button-1 ] == MouseState.CLICKED;
  }


  public boolean buttonDown( int button ) {
    return poll[ button-1 ] == MouseState.CLICKED ||
           poll[ button-1 ] == MouseState.PRESSED;
  }

  
  public synchronized void mousePressed( MouseEvent e ) {
    state[ e.getButton()-1 ] = true;
  }


  public synchronized void mouseReleased( MouseEvent e ) {
    state[ e.getButton()-1 ] = false;
  }


  public synchronized void mouseEntered( MouseEvent e ) {
    mouseMoved( e );
  }

  
  public synchronized void mouseExited( MouseEvent e ) {
    mouseMoved( e );
  }
  

  public synchronized void mouseDragged( MouseEvent e ) {
    mouseMoved( e );
  }


  public synchronized void mouseMoved( MouseEvent e ) {
    currentPos = e.getPoint();
  }

  
  public void mouseClicked( MouseEvent e ) {
    // Not needed
  }

}
