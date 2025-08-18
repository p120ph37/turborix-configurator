package com.zenoshrdlu.turborix;

/******************************************************************************
*                                                                             *
* Turborix Configurator User Interface (Calibrate)                            *
* Dave Mitchell dave@zenoshrdlu.com                                           *
*                                                                             *
******************************************************************************/

import java.awt.*;
import java.awt.List;

import java.awt.event.*;
import java.util.*;
import java.util.zip.*;
import java.io.*;
 
public class Calibrate extends Dialog implements ActionListener {

   static final String TITLE    = "Turborix Cailbration Screen";
   static final String START    = "Start Calibration";
   static final String NEXT     = "Next Step";
   static final String STOP     = "Stop Calibration";
   static final String CANCEL   = "Exit";
   static final String CRLF     = "\r\n";

   static final String STEP0INF = "To start click 'Start Calibration'"; 
   static final String STEP0    = "";
   static final String STEPEND  = CRLF +"then click NEXT";

   static final String STEP1INF = "Step 1 of 3";
   static final String STEP1A   = "  move Left Up/Down trimmer UP as far as possible";
   static final String STEP1B   = "  move Left Left/Right trimmer RIGHT as far as possible";
   static final String STEP1C   = "  move Right Up/Down trimmer UP as far as possible";
   static final String STEP1D   = "  move Right Left/Right trimmer RIGHT as far as possible";
   static final String STEP1E   = "  move BOTH joysticks UP and RIGHT as far as possible";
   static final String STEP1    = CRLF + STEP1A + CRLF + STEP1B + CRLF + STEP1C + CRLF + STEP1D + CRLF + STEP1E + CRLF + STEPEND; 

   static final String STEP2INF = "Step 2 of 3";
   static final String STEP2A   = "  move Left Up/Down trimmer DOWN as far as possible";
   static final String STEP2B   = "  move Left Left/Right trimmer LEFT as far as possible";
   static final String STEP2C   = "  move Right Up/Down trimmer DOWN as far as possible";
   static final String STEP2D   = "  move Right Left/Right trimmer LEFT as far as possible";
   static final String STEP2E   = "  move BOTH joysticks DOWN and LEFT as far as possible";
   static final String STEP2    = CRLF + STEP2A + CRLF + STEP2B + CRLF + STEP2C+ CRLF + STEP2D + CRLF + STEP2E + CRLF + STEPEND; 

   static final String STEP3INF = "Step 3 of 3";
   static final String STEP3A   = "  turn BOTH VR knobs fully CounterClockwise";
   static final String STEP3B   = "  turn BOTH VR knobs fully Clockwise";
   static final String STEP3    = CRLF + STEP3A + CRLF + STEP3B + CRLF + STEPEND; 

   static final String STEP4INF = "Now click 'Stop Calibration'"; 
   static final String STEP4    = "";
   
   static final String STEP5INF = "Calibration completed - click Exit";
   static final String STEP5    = "";

   
   TextField stepnum;
   TextArea  stepinfo;
   Button    start;
   Button    next;
   Button    stop;
   Button    cancel;
   

   TurborixConfig tui;
   TurborixEngine tei;
   int step = 0;

   public Calibrate(TurborixConfig ui, TurborixEngine te) {
      super(ui, true);
      Font f = new Font("SansSerif", Font.PLAIN, 12);
      setFont(f);
      tui = ui;
      tei = te;
      setTitle(TITLE);  

               
      setBackground(Color.lightGray);
      GridBagLayout gridbag = new GridBagLayout();
      setLayout(gridbag);

      GridBagConstraints c = new GridBagConstraints();
      c.insets = new Insets(2,2,2,2);
      
      c.fill = GridBagConstraints.HORIZONTAL;
      c.gridwidth  = 4;
      c.gridheight = 1;
      c.weightx = 4;
      c.weighty = 0;
      c.gridx = 1;
      c.gridy = 1;
      stepnum = new TextField("");
      stepnum.setBackground(Color.cyan);
      stepnum.setForeground(Color.black);
      stepnum.setEnabled(false);
      gridbag.setConstraints(stepnum,c);
      add(stepnum);
      
      c.fill = GridBagConstraints.BOTH;
      c.gridwidth  = 4;
      c.gridheight = 2;
      c.weightx = 4;
      c.weighty = 2;
      c.gridx = 1;
      c.gridy = 2;
      stepinfo = new TextArea(50,8);
      gridbag.setConstraints(stepinfo,c);
      add(stepinfo);
      setStep(0); 
              
      /* buttons */   
      c.fill = GridBagConstraints.NONE;
      c.gridwidth  = 1;
      c.gridheight = 1;
      c.weightx = 0;
      c.weighty = 0;
      c.gridx = 1;
      c.gridy = 5;
      start = new Button(START);
      start.addActionListener(this);
      gridbag.setConstraints(start,c);
      add(start);
      start.setEnabled(true);

      c.gridx = 2;
      next = new Button(NEXT);
      next.addActionListener(this);
      gridbag.setConstraints(next,c);
      add(next);
      next.setEnabled(false);

      c.gridx = 3;
      stop = new Button(STOP);
      stop.addActionListener(this);
      gridbag.setConstraints(stop,c);
      add(stop);
      stop.setEnabled(false);

      c.gridx = 4;
      cancel = new Button(CANCEL);
      cancel.addActionListener(this);
      gridbag.setConstraints(cancel,c);
      add(cancel);
      cancel.setEnabled(true);

      setStep(0); 
      setSize(400, 250);
      setLocation(150,150); 

   }


   public void setStep(int step) {
      if (step == 0) {
         stepnum.setText(STEP0INF);
         stepinfo.setText(STEP0);
      } else if (step == 1) {
         stepnum.setText(STEP1INF);
         stepinfo.setText(STEP1);
      } else if (step == 2) {
         stepnum.setText(STEP2INF);
         stepinfo.setText(STEP2);
      } else if (step == 3) {
         stepnum.setText(STEP3INF);
         stepinfo.setText(STEP3);
      } else if (step == 4) {
         stepnum.setText(STEP4INF);
         stepinfo.setText(STEP4);
      } else if (step == 5) {
         stepnum.setText(STEP5INF);
         stepinfo.setText(STEP5);
      }
   }
   
/*----------------------------------------------------------------------------*/
/*  handle a GUI action event                                                 */
/*----------------------------------------------------------------------------*/
   public void actionPerformed(ActionEvent event) {
      Object o = event.getSource();
      

      if (o == cancel) {
         setStep(0);
         dispose(); 
      } else if (o == start) {
         start.setEnabled(false);
         cancel.setEnabled(false);
         next.setEnabled(true);
         step  = 1;
         setStep(1);
         tei.startCalibrate();
      } else if (o == next) {
         if (step < 3) {
            step++;
            setStep(step);
         } else if (step == 3) {
            stop.setEnabled(true);
            next.setEnabled(false);
            step = 4;
            setStep(4);
         } else {   
            step = 5;
            setStep(5);
         }  
      } else if (o == stop) {
         start.setEnabled(true);
         cancel.setEnabled(true);
         stop.setEnabled(false);
         step = 0;
         setStep(5);
         tei.stopCalibrate();
      }
   }
}
