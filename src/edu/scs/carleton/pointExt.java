package edu.scs.carleton;

import java.awt.*;


/**
 * An applet that demonstrates the graph algorithm, by letting the user
 * pick the points in the screen and choose either the Quick Hull algor
 * or Brute Force algorithm, the program simulated the execution event,
 * showing which line or which point is being compared.
 *
 * @author Jeff C. So
 * @version %I%, %G%
 */

class pointExt extends Point {
	
	public pointExt(int x, int y) {
      super(x, y);
	}

   /** 
    * Draw a point.
    */
   public void draw(Graphics g, int size) {
      g.fillOval(x - 4, y - 4, size, size);
      g.setColor(Color.white);
      g.fillOval(x - 2, y - 2, 2, 2);
   }

   /** 
    * Draw the point being compared in different color and size.
    */
   public void blink(Graphics g) {
      g.setColor(Color.red);
      g.fillOval(x - 5, y - 5, 10, 10);
      g.setColor(Color.white);
      g.fillOval(x - 3, y - 3, 2, 2);
   }
   
}