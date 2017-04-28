package edu.scs.carleton;

import java.applet.Applet;
import java.applet.AudioClip;
import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.Checkbox;
import java.awt.Choice;
import java.awt.Event;
import java.awt.Panel;

public class ConvexHull extends Applet {
	   SrcPanel srcPanel;
	   Button   clearButton, doneButton, instruction;
	   Checkbox sound;
	   Choice   algorithms;
	   AudioClip ding, clank, getone, doneMusic, menuMusic;
	 
	   public void init() {
//	      ding      = getAudioClip(getCodeBase(), "audio/ding.au");
//	      clank     = getAudioClip(getCodeBase(), "audio/clank.au");
//	      getone    = getAudioClip(getCodeBase(), "audio/return.au");
//	      doneMusic = getAudioClip(getCodeBase(), "audio/done.au");
//	      menuMusic = getAudioClip(getCodeBase(), "audio/title.au");

	      setLayout(new BorderLayout());
	      srcPanel = new SrcPanel(this);
	      add("Center", srcPanel);
	      Panel ctrlPanel = new Panel();
	      add("North", ctrlPanel);

	      algorithms = new Choice();
	      algorithms.addItem("Brute Force");
	      algorithms.addItem("Quick Hull");
	      algorithms.addItem("Gift Wrap");
	      ctrlPanel.add(algorithms);

	      Choice speed = new Choice();
	      speed.addItem("Slow Demo");
	      speed.addItem("Fast Demo");
	      speed.addItem("No Delay");
	      ctrlPanel.add(speed);
//	      sound = new Checkbox("Sound");
//	      sound.setState(true);
//	      ctrlPanel.add(sound);
	      
	      clearButton = new Button("Clear");
	      ctrlPanel.add(clearButton);
	      doneButton  = new Button("Go....");
	      ctrlPanel.add(doneButton);

	      instruction = new Button("???");
	      ctrlPanel.add(instruction);
	    }

	   public void start() {
	      srcPanel.start();
	   }

	   public void stop() {
	      srcPanel.stop();
	   }

	   public boolean action(Event e, Object arg) {
	      if (e.target instanceof Button) {
	         if ((e.target).equals(instruction)) {
	            srcPanel.preAlgor = srcPanel.algor;
	            srcPanel.setMethod(SrcPanel.MENU);
	            srcPanel.runMode = true;
	         };

	         if ((e.target).equals(clearButton)) {
	            if (!srcPanel.runMode) {
	               srcPanel.clearPoint();
	            } else {
		       srcPanel.stop();
	               srcPanel.runMode = false;
	               srcPanel.clearPoint();
	               srcPanel.start();
	            }	
	         };

	         if ((e.target).equals(doneButton)) {
	            if (srcPanel.points.size() > 2 ) 
	               srcPanel.runMode = true;
	         };

	      };

	      if (e.target instanceof Choice) {
	         String choice = (String)arg;
	         if (choice.equals("Brute Force")) {
	            srcPanel.setMethod(SrcPanel.Brute);
	         } else if (choice.equals("Quick Hull")) {
	            srcPanel.setMethod(SrcPanel.QUICK);
	            }
	             else if (choice.equals("Gift Wrap")) {
		            srcPanel.setMethod(SrcPanel.Gift);
	         } else if (choice.equals("Slow Demo")) {
	            srcPanel.speed = SrcPanel.SLOW;
	         } else if (choice.equals("Fast Demo")) {
	            srcPanel.speed = SrcPanel.FAST;
		 } else if (choice.equals("No Delay")) {
		    srcPanel.speed = SrcPanel.ZERO;
	         }
	      };

//	      if (e.target instanceof Checkbox) {
//		 srcPanel.soundOn = ((Boolean)arg).booleanValue();
//	      };

	      return true;
	   }
	}

