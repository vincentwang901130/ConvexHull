package edu.scs.carleton;

import java.awt.Graphics;

class Line {
	   pointExt point1;
	   pointExt point2;
	   float    slope;
	   boolean  slopeUndefine;

	   /**
	    * Line constructor.
	    */
	   public Line(pointExt p1, pointExt p2) {
	      point1 = p1;
	      point2 = p2;
	      if (p1.x == p2.x)
	         slopeUndefine = true;
	      else {    
	         if (p2.y == p1.y)
	            slope = (float)0;
	         else
	            slope = (float) (p2.y - p1.y) / (p2.x - p1.x);
	         slopeUndefine = false;
	      }
	   }

	   /**
	    * Given a Check point and determine if this check point is lying on the
	    * left side or right side of the first point of the line.
	    */
	   public boolean onLeft(pointExt chkpt) {
	      if (this.slopeUndefine) {
	         if (chkpt.x < point1.x) return true;
	         else {
	            if (chkpt.x == point1.x) {
	               if (((chkpt.y > point1.y) && (chkpt.y < point2.y)) ||
	                   ((chkpt.y > point2.y) && (chkpt.y < point1.y)))
	                  return true;
	               else
	                  return false;
	            }
	            else return false;
	         }
	      }
	      else {            
	         /* multiply the result to avoid the rounding error */
	         int x3 = (int) (((chkpt.x + slope * (slope * point1.x 
	                          - point1.y + chkpt.y)) /
	                         (1 + slope * slope)) * 10000);
	         int y3 = (int) ((slope * (x3 / 10000 - point1.x) + point1.y) * 10000);

	         if (slope == (float)0) {
	            if ((chkpt.y*10000) > y3) return true; else return false; }
	         else { if (slope > (float)0) {
	                   if (x3 > (chkpt.x * 10000)) return true; else return false; }
	                else {
	                   if ((chkpt.x * 10000) > x3) return true; else return false; }
	              }
	      }
	   }

	   /**
	    * Draw a line.
	    */
	   public void draw(Graphics g) {
	      g.drawLine(point1.x, point1.y, point2.x, point2.y);
	   }
	}
