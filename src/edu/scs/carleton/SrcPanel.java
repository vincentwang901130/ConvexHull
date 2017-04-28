package edu.scs.carleton;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Event;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Panel;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;
import java.util.Vector;

class SrcPanel extends Panel implements Runnable {
	   ConvexHull Hull;
	   Thread     kicker;

	   /**
	    * Variable indicates we are calculating the hull
	    */
	   boolean runMode = false;

	   /** 
	    * Variable indicates which algorithm we are using
	    */
	   public static final int MENU  = 0;
	   public static final int Brute = 1;
	   public static final int QUICK = 2;
	   public static final int Gift  = 3;
	   int    algor = Brute;
	   int    preAlgor;

	   /**
	    * Variable indicates the demonstration speed
	    */
	   public static final int ZERO = 0;
	   public static final int FAST = 20;
	   public static final int SLOW = 100;
	   int    speed = SLOW;

	   /**
	    * Variable indicates the sound is on or off
	    */
	   boolean soundOn = true;

	   /**
	    * Stores all the points
	    */
	   Vector points = new Vector();

	   /**
	    * Stores all the lines in the Hull
	    */
	   Vector hull   = new Vector();

	   /**
	    * Stores all the lines being checking 
	    */
	   Vector chkLns = new Vector();
	   Vector tempLns = new Vector();
		
	   /**
	    * The point we are comparing with the chkLn
	    */
	   pointExt currPt     = new pointExt(0,0);
	   int cx, cy, cz;

	   public SrcPanel(ConvexHull Hull) {
	      this.Hull = Hull;
	   }

	   /**
	    * Detect the mouse down action and add the point
	    */
public synchronized boolean mouseDown(Event evt, int x, int y) {
	      if (!runMode) {
	         hull.removeAllElements();
	         points.addElement(new pointExt(x, y));
	         repaint();
	      } 
	      	else {
	    	  stop();
	    	  hull.removeAllElements();
	    	  points.addElement(new pointExt(x,y));
	    	  repaint();
	    	  start();
	      }

	      return true;
	  }

	   Image offscreen;
	   Dimension offscreensize;
	   Graphics offgraphics;

	   public void paint(Graphics g) {
	      Dimension d = size();
	      g.setColor(Color.white);
	      g.fillRect(0, 0, d.width, d.height);
	   }

	   /**
	    * Display all the points and line in the hull.
	    * If we are in execution mode, also display all the lines and points we are
	    * checking
	    */
	   public synchronized void update(Graphics g) {
	      Dimension d = size();
	      if ((offscreen == null) || (d.width != offscreensize.width) ||
	          (d.height != offscreensize.height)) {
	         offscreen = createImage(d.width, d.height);
	         offscreensize = d;
	         offgraphics = offscreen.getGraphics();
	      }

	      offgraphics.setColor(Color.white);
	      offgraphics.fillRect(0, 0, d.width-1, d.height-1);
	 
	      int np = points.size();
	      int nl = hull.size();

	      for (int i = 0; i < np; i++) {
	         Color ptcolor = (algor == MENU)? Color.lightGray : Color.blue;
	         offgraphics.setColor(ptcolor);
	         ((pointExt) points.elementAt(i)).draw(offgraphics, 8);
	      };

	      for (int j = 0; j < nl; j++) {
	         Color lncolor = (algor == MENU)? Color.lightGray : Color.blue;
	         offgraphics.setColor(lncolor);
	         ((Line) hull.elementAt(j)).draw(offgraphics);
	      }

	      if (runMode) {
	         currPt.blink(offgraphics);
	         offgraphics.setColor(Color.red);
		 for (int k = 0; k < chkLns.size(); k++) {
		    ((Line)chkLns.elementAt(k)).draw(offgraphics);
	         }  

	         offgraphics.setColor(Color.gray);

	      }

	      /* display a menu page */

	      if (algor == MENU) {

	         offgraphics.setColor(Color.red);
	         
	         Font font0 = new Font("TimesRoman", Font.ITALIC, 24);
	         offgraphics.setFont(font0);
	         offgraphics.drawString("Convex Hull", 50, 50);
	         Font font1 = new Font("TimesRoman", Font.ITALIC, 12);
	         offgraphics.setFont(font1);
	         offgraphics.drawString("Graph Algorithm Demonstration", 50, 80);

	         offgraphics.setFont(getFont());
	         offgraphics.setColor(Color.blue);
	         offgraphics.fillOval(44, 90, 8, 8);
	         offgraphics.setColor(Color.white);
	         offgraphics.fillOval(46, 92, 2, 2);
	         offgraphics.setColor(Color.red);
		 offgraphics.drawString("You pick those points", 60, 100);
	         
	         offgraphics.setColor(Color.red);
	         offgraphics.fillOval(45, 104, 10, 10);
	         offgraphics.setColor(Color.white);
	         offgraphics.fillOval(46, 106, 2, 2);
	         offgraphics.setColor(Color.red);
	         offgraphics.drawString("The program is checking this point", 60, 115);
	 
	         offgraphics.setColor(Color.blue);
	         offgraphics.drawLine(30, 125, 52, 125);
	         offgraphics.setColor(Color.red);
	         offgraphics.drawString("Lines in Convex Hull", 60, 130);

	         offgraphics.setColor(Color.red);
	         offgraphics.drawLine(30, 145, 52, 145);
	         offgraphics.drawString("The program is checking this line", 60, 150);

	         offgraphics.setColor(Color.red);
	         offgraphics.drawString("Orginal Version Programmed by : Jeff So and Modified by : Vince W", 50, 220);  

	         setMethod(preAlgor);
	      }

	      g.drawImage(offscreen, 0, 0, null);
	   }

	   /**
	    * Clear all the points and lines.
	    */
	   public void clearPoint() {
	      chkLns.removeAllElements();
	      hull.removeAllElements();
	      points.removeAllElements();
	      repaint();
	   }

	   /**
	    * Set up which algorithm to use.  
	    */
	   public void setMethod(int method) {
	      switch (method) {
	         case 0:
	            algor = MENU;
	            break;
	         case 1:
	            algor = Brute;
	            break;
	         case 2:
	            algor = QUICK;
	            break;
	         case 3:
		            algor = Gift;
		            break;
	         default:
	            algor = Brute;
	            break;
	      }
	   }

	   /**
	    * Run method
	    */
	   public void run() {
	      repaint();
	      while (true) {
	         if (runMode) {
	            switch (algor) {
	               case Brute: hull.removeAllElements();
	                           BruteForce();
	                           runMode = false;
				   repaint();
	                           break;
	               case QUICK: hull.removeAllElements();
	                           quickHull();
	                           runMode = false;
				   repaint();
	                           break;
	               case Gift: hull.removeAllElements();
	               				giftWrap();
                   runMode = false;
                   				repaint();
                   break;
	               case MENU:  runMode = false;
	                           repaint();
	                           break;
	               default:    System.out.println("Error in call algor\n");
	            }
	         
	         }

	         try { Thread.sleep(100); } catch (InterruptedException e) { break; }
	      }
	   }

	   public void start() {
	      kicker = new Thread(this);
	      kicker.setPriority(Thread.MAX_PRIORITY);
	      kicker.start();
	   }

	   public void stop() {
	      kicker.stop();
	   }
	   //check the C with vector AB
  
	  public int checkpoint(pointExt A, pointExt B, pointExt C){	  
		  int r=2;
		  float f = (B.x - A.x) * (C.y - A.y) - (C.x - A.x) * (B.y - A.y);
		  if(f>0)
			  r=1;
		  else if(f==0)
			  r=0;
		  else if(f<0)
			  r=-1;
		  return r;
	  }
	   
	  
	  /**
	   * 卷包裹法
	   * 从一个必然在凸包上的点开始向着一个方向依次选择最外侧的点
	   * 当回到最初的点时，所选出的点集就是所要求的凸包
	   */
	  public void giftWrap(){
		  int n = points.size();//所有点的个数
		  boolean[] isOnList = new boolean[n];//标记某个点是否已在凸包中
	    chkLns.removeAllElements();
	    tempLns.removeAllElements();
	    tempHull.removeAllElements();
	    Vector P1 = new Vector();//所有点的操作集合
	    Vector P2 = new Vector();//存储已经在凸包的点
	    for(int i=0;i<points.size();i++){
	    	//所有点放置到操作集合中
	    	P1.addElement(points.get(i));
	    }
	    //对操作集合进行排序,排序要求:按y升序排列,如果y相同则按x升序 
	    Collections.sort(P1, new Comparator<pointExt>() {
	    	public int compare(pointExt p1, pointExt p2) {
				if(p1.y>p2.y){
					return 1;
				}else if(p1.y==p2.y ){ 
			         if(p1.x>p2.x){ 
			        	 return 1;
			         }else if(p1.x==p2.x){
			        	 return 0;
			         }else{
			        	 return -1;
			         }
			   }else {
				  return -1;
			   }
			}
		});
	    //第一个极点是肯定在凸包上的, 暂时将第一个凸点标记为放进凸包
	    isOnList[0] = true;
	    int count = 0;
	    while(true){
	        int pointCount = -1;
	        for(int i=0;i<n;i++){
	        	//寻找一个不在当前凸包的点
	        	if(!isOnList[i]) {
	        		pointCount = i;
		            break;
	        	}
	        }
	        //如果所有的电都在凸包,则结束
	        if(pointCount == -1) {
	        	break;
	        }
	        for(int i=0;i<n;i++){ 
	        	  //遍历所有点,和现有最外侧的点比较,根据叉积和距离得到新的最外侧的点
		          if((cross((pointExt)P1.get(count),(pointExt)P1.get(i),(pointExt)P1.get(pointCount))>0) 
		        		  || (cross((pointExt)P1.get(count),(pointExt)P1.get(i),(pointExt)P1.get(pointCount)) == 0)
		        		  &&  (distance((pointExt)P1.get(count),(pointExt)P1.get(i)) > distance((pointExt)P1.get(pointCount), (pointExt)P1.get(pointCount))))  {
		        	 currPt = (pointExt)P1.get(i);
		        	 chkLns.addElement(new Line((pointExt)P1.get(count),(pointExt)P1.get(i)));
		        	 repaint();
		        	 try { Thread.sleep(speed); } catch (InterruptedException e) {}
		        	 pointCount = i;
		          }
	        }	
	        chkLns.removeAllElements();
	        repaint();
	        //这个最外侧的点已在凸包集合
	        if(isOnList[pointCount]) {
	        	break;
	        }
	        P2.addElement(P1.get(pointCount));//最外侧点加入到凸包
	        isOnList[pointCount] = true;//标记
	        count= pointCount;
	    }
	    //统计归零
	    count=0;
	    P2.addElement(P1.get(0));//将初始的极点加入凸包
	    //遍历所有的凸包点,进行绘图
	    for(int i=0;i<P2.size();i++){
	    	pointExt p = (pointExt)P2.get(i);
	    	pointExt p2;
	    	int tmp = i+1;
	    	if(i == P2.size()-1){
	    		tmp=0;
	    	}
	    	p2 = (pointExt)P2.get(tmp);
	    	hull.addElement(new Line(p, p2));
	    	repaint();
			try { Thread.sleep(speed); } catch (InterruptedException e) {}
	    }
	   
	  }
  
	  /**
	   * 获得向量CA与BA的叉积,用于计算角度问题
	   * @param c
	   * @param a
	   * @param b
	   * @return
	   */
   double cross(pointExt point1,pointExt point2,pointExt point3) {
   		return (point1.x-point2.x)*(point2.y-point3.y)-(point1.y-point2.y)*(point2.x-point3.x);
   }
   
   /**
    * 获得两点之间的距离
    * @param p1
    * @param p2
    * @return
    */
	public  double distance(pointExt point1, pointExt point2) {    
		return (Math.sqrt((point1.x - point2.x) * (point1.x- point2.x) + (point1.y - point2.y)* (point1.y - point2.y)));    
	}  
	   
	   
	   /**
	    * Brute Force Algorithm implementation
	    */
	public void BruteForce() {
	      boolean leftMost, rightMost;
	      for (cx = 0; cx < points.size(); cx++) {
	         for (int cy = (cx+1); cy < points.size(); cy++) {
	            leftMost  = true;
	            rightMost = true;
	            Line temp = new Line((pointExt) points.elementAt(cx),
	                                 (pointExt) points.elementAt(cy));

	            for (int cz = 0; cz < points.size(); cz++) {
	               currPt = (pointExt) points.elementAt(cz);
		       chkLns.removeAllElements();
	    	       chkLns.addElement(new Line((pointExt) points.elementAt(cx),
		   	         		  (pointExt) points.elementAt(cy)));

	               if ((cz != cx) && (cz != cy)) {
	                  if (temp.onLeft((pointExt) points.elementAt(cz))){
	                     leftMost = false;
	                  } else {
	                     rightMost = false;
	                  }
	                  repaint();
					  try { 
						  Thread.sleep(speed);
					  } catch (InterruptedException e) {
						  
					  }
	               }
	            }

	            if (leftMost || rightMost) {
	               hull.addElement(new Line((pointExt) points.elementAt(cx),
	                                        (pointExt) points.elementAt(cy)));
	            }
	         }
	      }
	      repaint();
	     
	   }

	   int indexChkLn = 0;

	   /** 
	    * Quick Hull Algorithm implementation.
	    * Calculate the hull first and display the execution with the information from
	    * chklns and tempHull.
	    */

	Vector tempHull = new Vector();

	public void quickHull() {
	      Vector P1 = new Vector();
	      Vector P2 = new Vector();
	      pointExt l = (pointExt)points.elementAt(0);
	      pointExt r = (pointExt)points.elementAt(0);
	      int minX = l.x;
	      int maxX = l.x;
	      int minAt = 0;
	      int maxAt = 0;	

	      chkLns.removeAllElements();
	      tempLns.removeAllElements();
	      tempHull.removeAllElements();
	      
	      /* find the max and min x-coord point */

	      for (int i = 1; i < points.size(); i++) {
	         currPt = (pointExt) points.elementAt(i);	
	         if (((pointExt)points.elementAt(i)).x > maxX) {
	            r = (pointExt)points.elementAt(i);
	            maxX = ((pointExt)points.elementAt(i)).x;
		    maxAt = i;
	         };

	         if (((pointExt)points.elementAt(i)).x < minX) {
	            l = (pointExt)points.elementAt(i);
	            minX = ((pointExt)points.elementAt(i)).x;
		    minAt = i;
	         };
		 repaint();
		 try { Thread.sleep(speed); } catch (InterruptedException e) {}

	      }

	      Line lr = new Line((pointExt) l, (pointExt) r);
	      tempLns.addElement(new Line((pointExt) points.elementAt(maxAt),
		   	                 (pointExt) points.elementAt(minAt)));
	      chkLns.addElement(new Line((pointExt) points.elementAt(maxAt),
		   	                 (pointExt) points.elementAt(minAt)));
	      repaint();
	      try { Thread.sleep(speed); } catch (InterruptedException e) {};

	      /* find out each point is over or under the line formed by the two points */
	      /* with min and max x-coord, and put them in 2 group according to whether */
	      /* they are above or under                                                */
	      for (int i = 0; i < points.size(); i++) {
		 if ((i != maxAt) && (i != minAt)) {
	            currPt = (pointExt) points.elementAt(i);

	            if (lr.onLeft((pointExt)points.elementAt(i))) {
	               P1.addElement(new pointExt(((pointExt)points.elementAt(i)).x,
	                                          ((pointExt)points.elementAt(i)).y));
	            } else {
	               P2.addElement(new pointExt(((pointExt)points.elementAt(i)).x,
	                                       ((pointExt)points.elementAt(i)).y));
	            }
	            repaint();
	            try { Thread.sleep(speed); } catch (InterruptedException e) {}
	         }
		
	      };
	      	
	      /* put the max and min x-cord points in each group */
	      P1.addElement(new pointExt(((pointExt)l).x, ((pointExt)l).y));
	      P1.addElement(new pointExt(((pointExt)r).x, ((pointExt)r).y));

	      P2.addElement(new pointExt(((pointExt)l).x, ((pointExt)l).y));
	      P2.addElement(new pointExt(((pointExt)r).x, ((pointExt)r).y));

	      /* calculate the upper hull */
	      quick(P1, l, r, 0);
	      
	      /* display the how the upper hull was calculated */
	      for (int i=0; i<tempLns.size(); i++) {
	        chkLns.addElement(new Line((pointExt) ((Line)tempLns.elementAt(i)).point1, 
	     	                           (pointExt) ((Line)tempLns.elementAt(i)).point2));
	        repaint();
	        try { Thread.sleep(speed); } catch (InterruptedException e) {break;};

		for (int j=0; j<points.size(); j++) {
	          if (((Line)tempLns.elementAt(i)).onLeft((pointExt)points.elementAt(j))) {	
	               currPt = (pointExt) points.elementAt(j);
		       repaint();
	               try { Thread.sleep(speed); } catch (InterruptedException e) {break;};
	          }
	        }
	      }

	      /* put the upper hull result in final result */
	      for (int k=0; k<tempHull.size(); k++) {
	         hull.addElement(new Line((pointExt) ((Line)tempHull.elementAt(k)).point1,
	                                  (pointExt) ((Line)tempHull.elementAt(k)).point2));
	      }		
	      chkLns.removeAllElements();
	      tempLns.removeAllElements();
	      
	      /* calculate the lower hull */
	      quick(P2, l, r, 1);

	      /* show how the lower hull was calculated */
	      for (int i=0; i<tempLns.size(); i++) {
	        chkLns.addElement(new Line((pointExt) ((Line)tempLns.elementAt(i)).point1, 
	     	                           (pointExt) ((Line)tempLns.elementAt(i)).point2));
	        repaint();
	        try { Thread.sleep(speed); } catch (InterruptedException e) {break;};

		for (int j=0; j<points.size(); j++) {
	          if (!((Line)tempLns.elementAt(i)).onLeft((pointExt)points.elementAt(j))) {	
	               currPt = (pointExt) points.elementAt(j);
		       repaint();
	               try { Thread.sleep(speed); } catch (InterruptedException e) {break;};
	          }
	        }
	      }
			
	      /* append the result from lower hull to final result */
	      for (int k=0; k<tempHull.size(); k++) {
	         hull.addElement(new Line((pointExt) ((Line)tempHull.elementAt(k)).point1,
	                                  (pointExt) ((Line)tempHull.elementAt(k)).point2));
	      }

	      chkLns.removeAllElements();
	  	  tempHull.removeAllElements();
	  	  tempLns.removeAllElements();

	   }

	   
	   
	   
	   
	 
	   
	   
	   
	   
	   
	   
	   
	   
	   
	   
	  
	   
	   
	   
	   
	   /**
	    * Recursive method to find out the Hull.
	    * faceDir is 0 if we are calculating the upper hull.
	    * faceDir is 1 if we are calculating the lower hull.
	    */
	   public synchronized void quick(Vector P, pointExt l, pointExt r, int faceDir) {
	      if (P.size() == 2) {
	         tempHull.addElement(new Line((pointExt) P.elementAt(0),
	                                  (pointExt) P.elementAt(1)));
	         return;
	      } else {
		 int hAt = splitAt(P, l, r);
	         Line lh = new Line((pointExt) l, (pointExt) P.elementAt(hAt));
	         Line hr = new Line((pointExt) P.elementAt(hAt), (pointExt) r);
	         Vector P1 = new Vector();
	         Vector P2 = new Vector();

	         for (int i = 0; i < (P.size() - 2); i++) {
		    if (i != hAt) {
	               currPt = (pointExt) P.elementAt(i);
		       if (faceDir == 0) {
	                  if (lh.onLeft((pointExt)P.elementAt(i))) {
	                     P1.addElement(new pointExt(((pointExt)P.elementAt(i)).x,
	                                                ((pointExt)P.elementAt(i)).y));
	                  }

			  if ((hr.onLeft((pointExt)P.elementAt(i)))) {
	                  P2.addElement(new pointExt(((pointExt)P.elementAt(i)).x,
	                                             ((pointExt)P.elementAt(i)).y));
	                  }
	 	       } else {
	                  if (!(lh.onLeft((pointExt)P.elementAt(i)))) {
	                     P1.addElement(new pointExt(((pointExt)P.elementAt(i)).x,
	                                                ((pointExt)P.elementAt(i)).y));
	                  };
		
		          if (!(hr.onLeft((pointExt)P.elementAt(i)))) {
	                  P2.addElement(new pointExt(((pointExt)P.elementAt(i)).x,
	                                             ((pointExt)P.elementAt(i)).y));
	                  }; 
		       };
	            }
	         }

	         P1.addElement(new pointExt(((pointExt)l).x, ((pointExt)l).y));
	         P1.addElement(new pointExt(((pointExt)P.elementAt(hAt)).x, 
					    ((pointExt)P.elementAt(hAt)).y));

	         P2.addElement(new pointExt(((pointExt)P.elementAt(hAt)).x, 
					    ((pointExt)P.elementAt(hAt)).y));
	         P2.addElement(new pointExt(((pointExt)r).x, ((pointExt)r).y));
		 
		 pointExt h = new pointExt(((pointExt)P.elementAt(hAt)).x,
				           ((pointExt)P.elementAt(hAt)).y);

	         tempLns.addElement(new Line((pointExt) l, (pointExt) h));
	         tempLns.addElement(new Line((pointExt) h, (pointExt) r));

	 	 if (faceDir == 0) {
	            quick(P1, l, h, 0);
	            quick(P2, h, r, 0);
		 } else {
		    quick(P1, l, h, 1);
	            quick(P2, h, r, 1);
	         }
	      return;
	      }
	   }

	   
	   
	   
	   
	   
	   
	   
	   
	   
	   
	   
	   
	   /**
	    * Find out a point which is in the Hull for sure among a group of points
	    * Since all the point are on the same side of the line formed by l and r,
	    * so the point with the longest distance perpendicular to this line is 
	    * the point we are lokking for.
	    * Return the index of this point in the Vector/
	    */
	   public synchronized int splitAt(Vector P, pointExt l, pointExt r) {
	      double    maxDist = 0;
	      Line newLn = new Line((pointExt) l, (pointExt) r);

	      int x3 = 0, y3 = 0;
	      double distance = 0;
	      int farPt = 0;

	      for (int i = 0; i < (P.size() - 2); i++) {
	         if (newLn.slopeUndefine) {
	            x3 = l.x;
	            y3 = ((pointExt)P.elementAt(i)).y;
	         } else {
	            if (r.y == l.y) {
	               x3 = ((pointExt)P.elementAt(i)).x;
	               y3 = l.y;
	            } else {
	                  x3 = (int) (((((pointExt)P.elementAt(i)).x + newLn.slope *
	                                (newLn.slope * l.x - l.y +
	                                ((pointExt)P.elementAt(i)).y))
	                              / (1 + newLn.slope * newLn.slope)));
	                  y3 = (int) ((newLn.slope * (x3 - l.x) + l.y));
	            }
	         }
	         int x1 = ((pointExt)P.elementAt(i)).x;
	         int y1 = ((pointExt)P.elementAt(i)).y;
	         distance = Math.sqrt(Math.pow((y1-y3), 2) + Math.pow((x1-x3), 2));

	         if (distance > maxDist) {
	            maxDist = distance;
	            farPt = i;
	         }
	      }
	      return farPt;
	   }
	}