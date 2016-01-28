import java.io.*;
import java.util.*;


public class Test {


	public static void main(String[] args) {
			Dog[] dogArray = new Dog[4];
			
			File spriteFolder = new File("res/dogSprites");
			String[] dogNames = spriteFolder.list();
			
			for (String name:dogNames) {
				System.out.println(name);
			}
			
			
			for (int i = 0; i < 4; i++) {
				// initialize new dogArray
				boolean nameTaken = false;
				// need escape in case all names taken/not finding >3 files?
				// check before loop, missing resources error? Fill index with same name.
				do {
					int indx = (int)(Math.random() * (dogNames.length + 1));
					if (dogNames[indx] != null) {
						String name = dogNames[indx].replace(".gif","");
						System.out.println(name);
						dogArray[i] = new Dog((name), i);
						dogNames[indx] = null;
					} else {
						nameTaken = true;
					}
				} while (nameTaken); 
				
			}
			
	}
}