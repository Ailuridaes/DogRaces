/*
	Modified from code by Tim Wright
	in Java Games: Keyboard and Mouse tutorial
	http://www.gamedev.net/page/resources/_/technical/general-programming/java-games-keyboard-and-mouse-r2439
*/
	
	
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;


public class KeyboardInput implements KeyListener {

	private static final int KEY_COUNT = 41;
	//'P' for pause is keycode 80 - use KEY_COUNT 81 to include.
	
	// Current state of the keyboard (down = true, up = false)
	private boolean[] state = null;
	// Polled keyboard state
	private KeyState[] keys = null; 
  
  private enum KeyState {
    RELEASED, // Not down
    PRESSED,  // Down, but not the first time
    ONCE      // Pressed since last poll
  }
      

  public KeyboardInput() {
	// Setup initial button states
    state = new boolean[ KEY_COUNT ];
    keys = new KeyState[ KEY_COUNT ];
    
	for( int i = 0; i < KEY_COUNT; ++i ) {
      keys[ i ] = KeyState.RELEASED;
    }

  }

        

  public synchronized void poll() {

	// Set the key state 
    for( int i = 0; i < KEY_COUNT; ++i ) {
	// If the key is down now, but was not
	// down last frame, set it to ONCE,
	// otherwise, set it to PRESSED

		if( state[ i ] ) {
			if( keys[ i ] == KeyState.RELEASED )
				keys[ i ] = KeyState.ONCE;
			else
				keys[ i ] = KeyState.PRESSED;
		} else {
			keys[ i ] = KeyState.RELEASED;
		}
    }
  }

        

  public boolean keyDown( int keyCode ) {
    return keys[ keyCode ] == KeyState.ONCE ||
           keys[ keyCode ] == KeyState.PRESSED;
  }


  public boolean keyDownOnce( int keyCode ) {
    return keys[ keyCode ] == KeyState.ONCE;
  }
      

  public synchronized void keyPressed( KeyEvent e ) {
    int keyCode = e.getKeyCode();

    if( keyCode >= 0 && keyCode < KEY_COUNT ) {
      state[ keyCode ] = true;
    }
  }


  public synchronized void keyReleased( KeyEvent e ) {
    int keyCode = e.getKeyCode();

    if( keyCode >= 0 && keyCode < KEY_COUNT ) {
      state[ keyCode ] = false;
    }
  }


  public void keyTyped( KeyEvent e ) {
    // Not needed
  }

}


/* examples:
	if( keyboard.keyDown( KeyEvent.VK_DOWN ) ) { ...
	if( keyboard.keyDownOnce( KeyEvent.VK_C ) ) { ...
	... KeyEvent.VK_SPACE ...
	
*/