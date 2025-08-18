package com.zenoshrdlu.turborix;

/******************************************************************************
*                                                                             *
* Turborix Configurator User Interface (Heli fields)                          *
* Dave Mitchell dave@zenoshrdlu.com                                           *
*                                                                             *
******************************************************************************/

import java.awt.*;
import java.awt.List;

import java.awt.event.*;
import java.util.*;
import java.util.zip.*;
import java.io.*;
 
public class HeliConfig extends Dialog implements ActionListener {

   static final String TITLE    = "Turborix Heli Configuration Screen";
   static final String THROTTLE = "Throttle";
   static final String PITCH    = "Pitch";
   static final String SWASH    = "SWASH AFR";
   static final String CHANNEL  = "Chan ";
   static final String NORMAL   = "Show Normal";
   static final String IDLE     = "Show Idle";
   static final String DONE     = "Done";
   static final String NORMIDLE = "          Normal Idle";
   
         
   BorderPanel bt, bp, bs;

   Label tni;
   Label t1;
   TextField t1n;
   TextField t1i;
   Label t2;
   TextField t2n;
   TextField t2i;
   Label t3;
   TextField t3n;
   TextField t3i;
   Label t4;
   TextField t4n;
   TextField t4i;
   Label t5;
   TextField t5n;
   TextField t5i;
   
   Label pni;
   Label p1;
   TextField p1n;
   TextField p1i;
   Label p2;
   TextField p2n;
   TextField p2i;
   Label p3;
   TextField p3n;
   TextField p3i;
   Label p4;
   TextField p4n;
   TextField p4i;
   Label p5;
   TextField p5n;
   TextField p5i;
   
   Label s1;
   Label s2;
   Label s3;
   TextField sw1;
   TextField sw2;
   TextField sw3;
   
   TextField message;
   Button    tbut;
   Button    pbut;
   
   boolean showtn = true;
   boolean showpn = true;
   
   GraphCanvas tgraph, pgraph;
   Button    done;
   
   SettingData oksd = null;
   
      int[] x;
  

   TurborixConfig tui;
   TurborixEngine te;

   public HeliConfig(TurborixConfig ui, TurborixEngine te) {
      super(ui, false);
      Font f = new Font("SansSerif", Font.PLAIN, 12);
      setFont(f);
      tui = ui;
      setTitle(TITLE);  

      x  = new int[5];
      x[0]=0;
      x[1]=25;
      x[2]=50;
      x[3]=75;
      x[4]=100;
               
      setBackground(Color.lightGray);
      GridBagLayout gridbag = new GridBagLayout();
      setLayout(gridbag);

      GridBagConstraints c = new GridBagConstraints();
      c.insets = new Insets(2,2,2,2);
      
      /* throttle panel */
      c.fill = GridBagConstraints.BOTH;
      c.gridwidth  = 6; 
      c.gridheight = 5; 
      c.weightx = 1;
      c.weighty = 1;
      c.gridx = 0;
      c.gridy = 1;
      bt = new BorderPanel(THROTTLE);
      gridbag.setConstraints(bt, c);
      add(bt);
 
      GridBagConstraints btc = new GridBagConstraints();
      GridBagLayout btgb = new GridBagLayout();
      bt.setLayout(btgb);
      
      btc.insets = new Insets(2,8,2,8);

      btc.fill = GridBagConstraints.HORIZONTAL;
      btc.gridwidth  = 3;
      btc.gridheight = 1;
      btc.weightx = 1;
      btc.weighty = 0;
      
      btc.gridx = 0;
        tni = new Label(NORMIDLE);
      btc.gridy = 0;
      btgb.setConstraints(tni, btc);
      bt.add(tni);
      
      Panel tnigrid = new Panel();
      GridLayout tnigl = new GridLayout(5,3);
      tnigrid.setLayout(tnigl);
      btc.fill = GridBagConstraints.HORIZONTAL;
      btc.gridx = 0;
      btc.gridy = 1;
      btc.gridwidth = 3;
      btc.gridheight = 5;
      btgb.setConstraints(tnigrid,btc);
      bt.add(tnigrid);
        t1 = new Label("P" + 1);
    tnigrid.add(t1);
        t1n = new TextField("",4);
    tnigrid.add(t1n);
        t1i = new TextField("",4);
    tnigrid.add(t1i);
         t2 = new Label("P" + 2);
    tnigrid.add(t2);
        t2n = new TextField("",4);
     tnigrid.add(t2n);
        t2i = new TextField("",4);
    tnigrid.add(t2i);
        t3 = new Label("P" + 3);
    tnigrid.add(t3);
        t3n = new TextField("",4);
     tnigrid.add(t3n);
        t3i = new TextField("",4);
     tnigrid.add(t3i);
        t4 = new Label("P" + 4);
    tnigrid.add(t4);
        t4n = new TextField("",4);
    tnigrid.add(t4n);
        t4i = new TextField("",4);
    tnigrid.add(t4i);
        t5 = new Label("P" + 5);
     tnigrid.add(t5);
        t5n = new TextField("",4);
    tnigrid.add(t5n);
        t5i = new TextField("",4);
     tnigrid.add(t5i);
      
         tgraph = new GraphCanvas();
      btc.fill = GridBagConstraints.BOTH;
      btc.gridx = 3;
      btc.gridy = 1;
      btc.gridwidth = 5;
      btc.gridheight = 5; 
      btgb.setConstraints(tgraph, btc);
      bt.add(tgraph);
      
      tbut = new Button(IDLE);
      tbut.addActionListener(this);
      btc.gridx = 3;
      btc.gridy = 6;
      btc.gridwidth = 1;
      btc.gridheight = 1;
      btc.fill = GridBagConstraints.HORIZONTAL;
      btgb.setConstraints(tbut, btc);
      bt.add(tbut);
 
      /* pitch panel */
      c.fill = GridBagConstraints.BOTH;
      c.gridwidth  = 6;
      c.gridheight = 5;
      c.weightx = 1;
      c.weighty = 1;
      c.gridx = 6;
      c.gridy = 1;
      bp = new BorderPanel(PITCH);
      gridbag.setConstraints(bp, c);
      add(bp);
 
      GridBagConstraints bpc = new GridBagConstraints();
      GridBagLayout bpgb = new GridBagLayout();
      bp.setLayout(bpgb);
      
      bpc.insets = new Insets(2,8,2,8);

      bpc.fill = GridBagConstraints.HORIZONTAL;
      bpc.gridwidth  = 3;
      bpc.gridheight = 1;
      bpc.weightx = 1;
      bpc.weighty = 0;

      bpc.gridx = 0;
        pni = new Label(NORMIDLE);
      bpc.gridy = 0;
      bpgb.setConstraints(pni, bpc);
      bp.add(pni);
      
      Panel pnigrid = new Panel();
      GridLayout pnigl = new GridLayout(5,3);
      pnigrid.setLayout(pnigl);
      bpc.fill = GridBagConstraints.HORIZONTAL;
      bpc.gridx = 0;
      bpc.gridy = 1;
      bpc.gridwidth = 3;
      bpc.gridheight = 5;
      bpgb.setConstraints(pnigrid,bpc);
      bp.add(pnigrid);
      
        p1 = new Label("P" + 1);
    pnigrid.add(p1);
        p1n = new TextField("",4);
      pnigrid.add(p1n);
        p1i = new TextField("",4);
       pnigrid.add(p1i);
        p2 = new Label("P" + 2);
    pnigrid.add(p2);
        p2n = new TextField("",4);
       pnigrid.add(p2n);
        p2i = new TextField("",4);
      pnigrid.add(p2i);
        p3 = new Label("P" + 3);
    pnigrid.add(p3);
        p3n = new TextField("",4);
      pnigrid.add(p3n);
        p3i = new TextField("",4);
      pnigrid.add(p3i);
        p4 = new Label("P" + 4);
    pnigrid.add(p4);
       p4n = new TextField("",4);
      pnigrid.add(p4n);
        p4i = new TextField("",4);
      pnigrid.add(p4i);
        p5 = new Label("P" + 5);
    pnigrid.add(p5);
        p5n = new TextField("",4);
      pnigrid.add(p5n);
        p5i = new TextField("",4);
      pnigrid.add(p5i);
      
         pgraph = new GraphCanvas();
      bpc.fill = GridBagConstraints.BOTH;
      bpc.gridx = 3;
      bpc.gridy = 1;
      bpc.gridwidth = 5;
      bpc.gridheight = 5;
      bpgb.setConstraints(pgraph, bpc);
      bp.add(pgraph);

      pbut = new Button(IDLE);
      pbut.addActionListener(this);
      bpc.gridx = 3;
      bpc.gridy = 6;
      bpc.gridheight = 1;
      bpc.gridwidth = 1;
      bpc.fill = GridBagConstraints.HORIZONTAL;
      bpgb.setConstraints(pbut, bpc);
      bp.add(pbut);
      
      
      
      /* swash panel */
      c.fill = GridBagConstraints.BOTH;
      c.gridwidth  = 3;
      c.gridheight = 3;
      c.weightx = 1;
      c.weighty = 1;
      c.gridx = 5;
      c.gridy = 6;
      bs = new BorderPanel(SWASH);
      gridbag.setConstraints(bs, c);
      add(bs);
 
      GridBagConstraints bsc = new GridBagConstraints();
      GridBagLayout bsgb = new GridBagLayout();
      bs.setLayout(bsgb);
      
      bsc.insets = new Insets(2,8,2,8);
      
      bsc.weightx = 0;
      bsc.weighty = 0;
 
  
      Panel swgrid = new Panel();
      GridLayout swgl = new GridLayout(2,3);
      swgrid.setLayout(swgl);

      bsc.fill = GridBagConstraints.HORIZONTAL;
      bsc.gridx = 0;
      bsc.gridy = 1;
      bsc.gridwidth = 3;
      bsc.gridheight = 3;
      bsgb.setConstraints(swgrid,bsc);
      bs.add(swgrid);

    
      s1 = new Label(CHANNEL + 1);
      swgrid.add(s1);
      s2 = new Label(CHANNEL + 2);
      swgrid.add(s2);
      s3 = new Label(CHANNEL + 3);
      swgrid.add(s3);
      sw1 = new TextField("",4);
      swgrid.add(sw1);
      sw2 = new TextField("",4);
      swgrid.add(sw2);
      sw3 = new TextField("",4);
      swgrid.add(sw3);
      
      
      /* done button */   
      c.fill = GridBagConstraints.NONE;
      c.gridwidth  = 1;
      c.gridheight = 1;
      c.weightx = 0;
      c.weighty = 0;
      c.gridx = 5;
      c.gridy = 11;
      done = new Button(DONE);
      done.addActionListener(this);
      gridbag.setConstraints(done,c);
      add(done);
  
      setSize(600, 320);
      setLocation(150,150); 
//      setVisible(true);

   }

   public void setMessage(String t) {
//      message.setText(t);
   }
   
   public void applySettings(SettingData sdata) {
      t1n.setText("" + sdata.throttlecurve[0][0]);
      t1i.setText("" + sdata.throttlecurve[0][1]);
      t2n.setText("" + sdata.throttlecurve[1][0]);
      t2i.setText("" + sdata.throttlecurve[1][1]);
      t3n.setText("" + sdata.throttlecurve[2][0]);
      t3i.setText("" + sdata.throttlecurve[2][1]);
      t4n.setText("" + sdata.throttlecurve[3][0]);
      t4i.setText("" + sdata.throttlecurve[3][1]);
      t5n.setText("" + sdata.throttlecurve[4][0]);
      t5i.setText("" + sdata.throttlecurve[4][1]);
      
      p1n.setText("" + sdata.pitchcurve[0][0]);
      p1i.setText("" + sdata.pitchcurve[0][1]);
      p2n.setText("" + sdata.pitchcurve[1][0]);
      p2i.setText("" + sdata.pitchcurve[1][1]);
      p3n.setText("" + sdata.pitchcurve[2][0]);
      p3i.setText("" + sdata.pitchcurve[2][1]);
      p4n.setText("" + sdata.pitchcurve[3][0]);
      p4i.setText("" + sdata.pitchcurve[3][1]);
      p5n.setText("" + sdata.pitchcurve[4][0]);
      p5i.setText("" + sdata.pitchcurve[4][1]);
      sw1.setText("" + sdata.swash[0]);
      sw2.setText("" + sdata.swash[1]);
      sw3.setText("" + sdata.swash[2]);
      validateFields();
      showGraphs();
   }
   
   public void buildSettings(int[] sd) {
     int i;
     if (validateFields() != 0) {
         tui.setMessage("" + validateFields() + " heli value(s) invalid");
         return;
     }
     i = getInt(sw1.getText());
     sd[8] = (i >= 0 ? i : i + 256);
     i = getInt(sw2.getText());
     sd[9] = (i >= 0 ? i : i + 256);
     i = getInt(sw3.getText());
     sd[10] = (i >= 0 ? i : i + 256);
     
     i = getInt(t1n.getText());
     sd[23] = (i >= 0 ? i : i+256);
     i = getInt(t1i.getText());
     sd[24] = (i >= 0 ? i : i+256);
     i = getInt(t2n.getText());
     sd[25] = (i >= 0 ? i : i+256);
     i = getInt(t2i.getText());
     sd[26] = (i >= 0 ? i : i+256);
     i = getInt(t3n.getText());
     sd[27] = (i >= 0 ? i : i+256);
     i = getInt(t3i.getText());
     sd[28] = (i >= 0 ? i : i+256);
     i = getInt(t4n.getText());
     sd[29] = (i >= 0 ? i : i+256);
     i = getInt(t4i.getText());
     sd[30] = (i >= 0 ? i : i+256);
     i = getInt(t5n.getText());
     sd[31] = (i >= 0 ? i : i+256);
     i = getInt(t5i.getText());
     sd[32] = (i >= 0 ? i : i+256);
     
     i = getInt(p1n.getText());
     sd[33] = (i >= 0 ? i : i+256);
     i = getInt(p1i.getText());
     sd[34] = (i >= 0 ? i : i+256);
     i = getInt(p2n.getText());
     sd[35] = (i >= 0 ? i : i+256);
     i = getInt(p2i.getText());
     sd[36] = (i >= 0 ? i : i+256);
     i = getInt(p3n.getText());
     sd[37] = (i >= 0 ? i : i+256);
     i = getInt(p3i.getText());
     sd[38] = (i >= 0 ? i : i+256);
     i = getInt(p4n.getText());
     sd[39] = (i >= 0 ? i : i+256);
     i = getInt(p4i.getText());
     sd[40] = (i >= 0 ? i : i+256);
     i = getInt(p5n.getText());
     sd[41] = (i >= 0 ? i : i+256);
     i = getInt(p5i.getText());
     sd[42] = (i >= 0 ? i : i+256);
   }

   public void notifySettings(SettingData sdata) {
      applySettings(sdata);   
   }
   
   public int flagError(TextField t, boolean e) {
      t.setBackground(e? Color.red : Color.cyan);
      return e ? 1 : 0;
   }
   
   public int validateField(TextField t, int lo, int hi) {
      String s;
      int i;
      int e;
      s = t.getText();
      i = getInt(s);
       e = flagError(t, (i < lo || i > hi));
      if (e == 1)   
         tui.addLogMsg("Field " + t + " value " + i + " out of range " + lo + ":" + hi);
      return e;
   }
   
   public int validateFields() {
      int j;

      j = 0;
      j += validateField(t1n,-100,100);
      j += validateField(t1i,-100,100);
      j += validateField(t2n,-100,100);
      j += validateField(t2i,-100,100);
      j += validateField(t3n,-100,100);
      j += validateField(t3i,-100,100);
      j += validateField(t4n,-100,100);
      j += validateField(t4i,-100,100);
      j += validateField(t5n,-100,100);
      j += validateField(t5i,-100,100);
      j += validateField(p1n,-100,100);
      j += validateField(p1i,-100,100);
      j += validateField(p2n,-100,100);
      j += validateField(p2i,-100,100);
      j += validateField(p3n,-100,100);
      j += validateField(p3i,-100,100);
      j += validateField(p4n,-100,100);
      j += validateField(p4i,-100,100);
      j += validateField(p5n,-100,100);
      j += validateField(p5i,-100,100);
      j += validateField(sw1,-100,100);
      j += validateField(sw2,-100,100);
      j += validateField(sw3,-100,100);
      return j;
   }
   
   public int[] getTYPoints(boolean norm) {
      int[] y;
      y = new int[5];
      if (norm) {
         y[0] = getInt(t1n.getText());
         y[1] = getInt(t2n.getText());
         y[2] = getInt(t3n.getText());
         y[3] = getInt(t4n.getText());
         y[4] = getInt(t5n.getText());
      } else {
         y[0] = getInt(t1i.getText());
         y[1] = getInt(t2i.getText());
         y[2] = getInt(t3i.getText());
         y[3] = getInt(t4i.getText());
         y[4] = getInt(t5i.getText());
      }
      return y;
   }
   
   public int[] getPYPoints(boolean norm) {
      int[] y;
      y = new int[5];
      if (norm) {
         y[0] = getInt(p1n.getText());
         y[1] = getInt(p2n.getText());
         y[2] = getInt(p3n.getText());
         y[3] = getInt(p4n.getText());
         y[4] = getInt(p5n.getText());
      } else {
         y[0] = getInt(p1i.getText());
         y[1] = getInt(p2i.getText());
         y[2] = getInt(p3i.getText());
         y[3] = getInt(p4i.getText());
         y[4] = getInt(p5i.getText());
      }
      return y;
   }
   
   public int getInt(String s) {
      try {
         int i = Integer.parseInt(s);
         return i;
      } catch (Exception e) {
         return -999;
      }
   }
   
   public void showGraphs() {         
      tbut.setLabel(showtn? IDLE : NORMAL);
      tgraph.setFill(showtn ? Color.green: Color.red);
      tgraph.setPoints(x,getTYPoints(showtn));
      pbut.setLabel(showpn? IDLE : NORMAL);
      pgraph.setFill(showpn ? Color.green: Color.red);
      pgraph.setPoints(x,getPYPoints(showpn));
   }

/*----------------------------------------------------------------------------*/
/*  handle a GUI action event                                                 */
/*----------------------------------------------------------------------------*/
   public void actionPerformed(ActionEvent event) {
      boolean ok;
      int[] sd;
      Object o = event.getSource();
      

      if (o == done) {
         dispose(); 
      } else if (o == tbut) {
         int i = validateFields();
         showtn = !showtn;
         tbut.setLabel(showtn? IDLE : NORMAL);
         tgraph.setFill(showtn ? Color.green: Color.red);
         tgraph.setPoints(x,getTYPoints(showtn));
      } else if (o == pbut) {
         int i = validateFields();
         showpn = !showpn;
         pbut.setLabel(showpn? IDLE : NORMAL);
         pgraph.setFill(showpn ? Color.green: Color.red);
         pgraph.setPoints(x,getPYPoints(showpn));
      }
   }
}
