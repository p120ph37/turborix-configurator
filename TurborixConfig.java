/******************************************************************************
*                                                                             *
* Turborix Configurator User Interface                                        *
* Dave Mitchell dave@zenoshrdlu.com                                           *
*                                                                             *
******************************************************************************/

// v 1.01 - try SI port if Prolific fails
// v 1.02 - add missing heli stuff
// v 1.03 - swap model and stick!
// v 1.04 - fix endpoint limits at 120 not 100!
// v 1.05 ??
// v 1.06 - add Calibration 
// v 1.06X - remove apple event dependency

import java.awt.*;
import java.awt.List;

import java.awt.event.*;
import java.util.*;
import java.util.zip.*;
import java.io.*;
 
public class TurborixConfig extends Frame implements TurborixUI, 
                                                     WindowListener,
                                                     ItemListener,
                                                     ActionListener {

   static final String TITLE    = "Turborix Configurator - Dave Mitchell";
   static final String VERSION  = "1.06X";
   static final String CRLF     = "\r\n";
   static final String GET      = "Get Settings";
   static final String SEND     = "Send Settings";
   static final String SAVE     = "Save Settings";
   static final String RESTORE  = "Restore Settings";
   static final String CALIBRATE= "Calibrate";

   static final String EXIT     = "Exit";
   static final String CDATA    = " Channel Data ";
   static final String SDATA1   = "      Normal/Reverse       End Points       SubTrim ";
   static final String SDATA2   = " Dual Rate    On           Off  ";
   static final String SDATA3   = " Switches & Variables ";
   static final String SDATA4   = " Mixes ";
   static final String SDATA5   = "    Mix 1                         Mix 2                          Mix 3 ";

   static final String STICK    = "Stick";
   static final String MODE     = "Mode ";
   static final String ACRO     = "ACRO";
   static final String HELI120  = "HELI-120";
   static final String HELI90   = "HELI-90";
   static final String HELI140  = "HELI-140";
   
   static final String CHANNEL  = "Channel ";
   static final String CONNECTED= "Connected";
   static final String SYNCED   = "Synced";
   static final String CH       = "Ch ";  
   static final String NORMAL   = "N";
   static final String REVERSE  = "R";
   static final String SWITCH   = "Switch ";
   static final String VARIABLE = "Variable ";
   static final String NULL     = "Null";
   static final String DUAL     = "DualRate";
   static final String THROT    = "ThrottleCut";
   static final String NORMID   = "Normal/Idle";
   static final String ADJUST   = "Adjust";
   static final String SOURCE   = "Source";
   static final String DEST     = "Destination";
   static final String UP       = "Up Rate";
   static final String DOWN     = "Down Rate";
   static final String ON       = "On";
   static final String OFF      = "Off";
   
   static final String PROPS    = "turborix.properties";
   static final String PROPT    = "Turborix Properties - Dave Mitchell (dave@zenoshrdlu.com)";
   static final String HPROP    = "height";
   static final String WPROP    = "width";
   static final int    DEFH     = 700;
   static final int    DEFW     = 785;
   static final String PPROP    = "port";
   static final String LPROP    = "library"; 
   static final String DEFPORT  =  "/dev/tty.usbserial";
   static final String SIPORT   = "/dev/tty.SLAB_USBtoUART";
   static final String DEFLIB   = "rxtxSerial";
   static final String USERDIR  = "user.dir";
   static final String SAVEDLG  = "Save Settings ...";
   static final String RESTDLG  = "Restore Settings ...";
   
   static final String SAVEDAS  = "Saved As";
   static final String LOADFRM  = "Loaded From";
   static final String TRANSMIT = "transmitter";
   
   
   static final String LOGGING  = "logging";
   static final String YES      = "YES";
   static final String NO       = "NO";
   static final String LOGPROP  = "logfile";
   static final String FLDPROP  = "folder";
   
   static final String DEFLOG   = "tlog.txt";
         
   Label lm;
   Label lsk;
   Choice stick;
   Choice mode;
   Label l1;
   Label l2;
   Label l3;
   Label l4;
   Label l5;
   Label l6;
   Label lc;
   Label ls;
   Label ln;
   TextField fn; 
   
   TextField ch1;
   TextField ch2;
   TextField ch3;
   TextField ch4;
   TextField ch5;
   TextField ch6;
   Scrollbar hchscroll1;
   Scrollbar hchscroll2;
   Scrollbar hchscroll3;
   Scrollbar hchscroll4;
   Scrollbar hchscroll5;
   Scrollbar hchscroll6;
   
   LEDCanvas connected;
   LEDCanvas synced;
   BorderPanel b, bs1, bs2, bs3, bs4;   
   
   Label ls1;
   Label ls2;
   Label ls3;
   Label ls4;
   Label ls5;
   Label ls6;
   
   Choice nr1;
   TextField ep1a;
   TextField ep1b;
   Choice nr2;
   TextField ep2a;
   TextField ep2b;
   Choice nr3;
   TextField ep3a;
   TextField ep3b;
   Choice nr4;
   TextField ep4a;
   TextField ep4b;
   Choice nr5;
   TextField ep5a;
   TextField ep5b;
   Choice nr6;
   TextField ep6a;
   TextField ep6b;
   
   TextField subt1;
   TextField subt2;
   TextField subt3;
   TextField subt4;
   TextField subt5;
   TextField subt6;
   
   Label ld1;
   Label ld2;
   Label ld3;
   
   TextField dualon1;
   TextField dualoff1;
   TextField dualon2;
   TextField dualoff2;
   TextField dualon4;
   TextField dualoff4;
   
   Label lswa;
   Label lswb;
   
   Choice switcha;
   Choice switchb;
   
   Label lvara;
   Label lvarb;
   
   Choice variablea;
   Choice variableb;
   
  
   Label lmsrc;
   Label lmdest;
   Label lmup;
   Label lmdown;
   Label lmswitch;
   
   Choice mix1src;
   Choice mix1dest;
   TextField mix1up;
   TextField mix1down;
   Choice mix1sw;
   
   Choice mix2src;
   Choice mix2dest;
   TextField mix2up;
   TextField mix2down;
   Choice mix2sw;
   
   Choice mix3src;
   Choice mix3dest;
   TextField mix3up;
   TextField mix3down;
   Choice mix3sw;
   
   
   TextField message;
   Button    get;
   Button    send;
   Button    save;
   Button    restore;
   Button    calibrate;
   Button    exit;
   
   SettingData oksd = null;

   Properties props   = new Properties();
   boolean loaded = false;
   int		  hprop;
   int		  wprop;
   
   String libname;
   String portname;
   String portname2;
   
   TurborixEngine te;
   HeliConfig hc;
   Calibrate cdlg;
   boolean cok = false;
   boolean sok = false;
   
   boolean logging = true;
   String logfile  = null;
   String folder   = null; 
   
   FileDialog filedialog;
   DataOutputStream logOut = null;


   public TurborixConfig() {
      addWindowListener(this);
      Font f = new Font("SansSerif", Font.PLAIN, 12);
      setFont(f);
      
      setTitle(TITLE + " - " + VERSION);  
      filedialog = new FileDialog(this);

      folder = getProperty(FLDPROP);
      if (folder.length() == 0) {
        folder = System.getProperty(USERDIR);
      }
      
      String l = getProperty(LOGGING);
      String lf = getProperty(LOGPROP);
      
      if (l.length() > 0) {
         if (l.toUpperCase().equals(YES)) 
            logging = true;
         else if (l.toUpperCase().equals(NO)) 
            logging = false;
      }
   
      if (lf.length() == 0) {
            lf = DEFLOG;
      }
      if (logging) {      
         openLog(lf);
      }
      
      setBackground(Color.lightGray);
      GridBagLayout gridbag = new GridBagLayout();
      setLayout(gridbag);

      GridBagConstraints c = new GridBagConstraints();
      c.insets = new Insets(2,2,2,2);
      
      c.fill = GridBagConstraints.BOTH;
      c.gridwidth  = 6; 
      c.gridheight = 8;
      c.weightx = 1;
      c.weighty = 2;
      c.gridx = 0;
      c.gridy = 1;
      b = new BorderPanel(CDATA);
      gridbag.setConstraints(b, c);
      add(b);
 
      GridBagConstraints bc = new GridBagConstraints();

      GridBagLayout bgb = new GridBagLayout();
      b.setLayout(bgb);
      
      bc.insets = new Insets(2,8,2,8);

      bc.fill = GridBagConstraints.HORIZONTAL;
      bc.gridwidth  = 1;
      bc.gridheight = 1;
      bc.weighty = 0;
   
      /* ch 1 */
      bc.gridwidth  = 1;
      ch1 = new TextField("", 4);
      ch1.setBackground(Color.cyan);
      ch1.setForeground(Color.black);
      bc.weightx = 0;
      ch1.setEnabled(false);
      bc.gridx = 0;
      bc.gridy = 1;
      l1 = new Label(CHANNEL + 1);
      bgb.setConstraints(l1,bc);
      b.add(l1);
      bc.gridx = 1;
      bgb.setConstraints(ch1,bc);
      b.add(ch1);
      bc.weightx = 1;
      hchscroll1 = new Scrollbar(Scrollbar.HORIZONTAL,0,1,-100,100);
      bc.gridx = 2;
      bc.gridwidth  = 4;
      bc.fill = GridBagConstraints.HORIZONTAL;
      bgb.setConstraints(hchscroll1,bc);
      b.add(hchscroll1);


      /*ch 2 */
      bc.gridwidth  = 1;
      ch2 = new TextField("", 4);
      ch2.setBackground(Color.cyan);
      ch2.setForeground(Color.black);
      bc.weightx = 0;
      ch2.setEnabled(false);
      bc.gridx = 0;
      bc.gridy = 2;
      l2 = new Label(CHANNEL + 2);
      bgb.setConstraints(l2,bc);
      b.add(l2);
      bc.gridx = 1;
      bgb.setConstraints(ch2,bc);
      b.add(ch2);
      bc.weightx = 1;
      hchscroll2 = new Scrollbar(Scrollbar.HORIZONTAL,0,1,-100,100);
      bc.gridx = 2;
      bc.gridwidth  = 4;
      bc.fill = GridBagConstraints.HORIZONTAL;
      bgb.setConstraints(hchscroll2,bc);
      b.add(hchscroll2);


      /* ch 3 */
      bc.gridwidth  = 1;
      ch3 = new TextField("", 4);
      ch3.setBackground(Color.cyan);
      ch3.setForeground(Color.black);
      bc.weightx = 0;
      ch3.setEnabled(false);
      bc.gridx = 0;
      bc.gridy = 3;
      l3 = new Label(CHANNEL + 3);
      bgb.setConstraints(l3,bc);
      b.add(l3);
      bc.gridx = 1;
      bgb.setConstraints(ch3,bc);
      b.add(ch3);
      bc.weightx = 1;
      hchscroll3 = new Scrollbar(Scrollbar.HORIZONTAL,0,1,-100,100);
      bc.gridx = 2;
      bc.gridwidth  = 4;
      bc.fill = GridBagConstraints.HORIZONTAL;
      bgb.setConstraints(hchscroll3,bc);
      b.add(hchscroll3);


      /* ch 4 */
      bc.gridwidth  = 1;
      ch4 = new TextField("", 4);
      ch4.setBackground(Color.cyan);
      ch4.setForeground(Color.black);
      bc.weightx = 0;
      ch4.setEnabled(false);
      bc.gridx = 0;
      bc.gridy = 4;
      l4 = new Label(CHANNEL + 4);
      bgb.setConstraints(l4,bc);
      b.add(l4);
      bc.gridx = 1;
      bgb.setConstraints(ch4,bc);
      b.add(ch4);
      bc.weightx = 1;
      hchscroll4 = new Scrollbar(Scrollbar.HORIZONTAL,0,1,-100,100);
      bc.gridx = 2;
      bc.gridwidth  = 4;
      bc.fill = GridBagConstraints.HORIZONTAL;
      bgb.setConstraints(hchscroll4,bc);
      b.add(hchscroll4);


      /* ch 5 */
      bc.gridwidth  = 1;
      ch5 = new TextField("", 4);
      ch5.setBackground(Color.cyan);
      ch5.setForeground(Color.black);
      bc.weightx = 0;
      ch5.setEnabled(false);
      bc.gridx = 0;
      bc.gridy = 5;
      l5 = new Label(CHANNEL + 5);
      bgb.setConstraints(l5,bc);
      b.add(l5);
      bc.gridx = 1;
      bgb.setConstraints(ch5,bc);
      b.add(ch5);
      bc.weightx = 1;
      hchscroll5 = new Scrollbar(Scrollbar.HORIZONTAL,0,1,-100,100);
      bc.gridx = 2;
      bc.gridwidth  = 4;
      bc.fill = GridBagConstraints.HORIZONTAL;
      bgb.setConstraints(hchscroll5,bc);
      b.add(hchscroll5);


      /* ch 6 */
      bc.gridwidth  = 1;
      ch6 = new TextField("", 4);
      ch6.setBackground(Color.cyan);
      ch6.setForeground(Color.black);
      bc.weightx = 0;
      ch6.setEnabled(false);
      bc.gridx = 0;
      bc.gridy = 6;
      l6 = new Label(CHANNEL + 6);
      bgb.setConstraints(l6,bc);
      b.add(l6);
      bc.gridx = 1;
      bgb.setConstraints(ch6,bc);
      b.add(ch6);
      bc.weightx = 1;
      hchscroll6 = new Scrollbar(Scrollbar.HORIZONTAL,0,1,-100,100);
      bc.gridx = 2;
      bc.gridwidth  = 4;
      bc.fill = GridBagConstraints.HORIZONTAL;
      bgb.setConstraints(hchscroll6,bc);
      b.add(hchscroll6);

      
      /* normal/reverse and end points */
      c.fill = GridBagConstraints.BOTH;
      c.gridwidth  = 3;
      c.gridheight = 8;
      c.weightx = 1;
      c.weighty = 5; //1;
      c.gridx = 0;
      c.gridy = 9;
      bs1 = new BorderPanel(SDATA1);
      gridbag.setConstraints(bs1, c);
      add(bs1);
 
      GridBagConstraints bs1c = new GridBagConstraints();
      GridBagLayout bgbs1 = new GridBagLayout();
      bs1.setLayout(bgbs1);
      
      bs1c.insets = new Insets(2,8,2,8);

      bs1c.fill = GridBagConstraints.HORIZONTAL;
      bs1c.gridwidth  = 1;
      bs1c.gridheight = 1;
      bs1c.weighty = 0;
      
      bs1c.gridx = 0;
      bs1c.gridy = 1;
      ls1 = new Label(CH + 1);
      bgbs1.setConstraints(ls1,bs1c);
      bs1.add(ls1);
      
      nr1 = new Choice();
      nr1.add(NORMAL);
      nr1.add(REVERSE);
      bs1c.gridx = 1;
      bgbs1.setConstraints(nr1,bs1c);
      bs1.add(nr1);
      ep1a = new TextField("",4);
      ep1a.setBackground(Color.cyan);
      ep1a.setForeground(Color.black);
      bs1c.gridx = 2;
      bgbs1.setConstraints(ep1a,bs1c);
      bs1.add(ep1a);
      ep1b = new TextField("",4);
      ep1b.setBackground(Color.cyan);
      ep1b.setForeground(Color.black);
      bs1c.gridx =3;
      bgbs1.setConstraints(ep1b,bs1c);
      bs1.add(ep1b);
      
      bs1c.gridy = 2;
      bs1c.gridx = 0;
      ls2 = new Label(CH + 2);
      bgbs1.setConstraints(ls2,bs1c);
      bs1.add(ls2);
      nr2 = new Choice();
      nr2.add(NORMAL);
      nr2.add(REVERSE);
      bs1c.gridx = 1;
      bgbs1.setConstraints(nr2,bs1c);
      bs1.add(nr2);
      ep2a = new TextField("",4);
      ep2a.setBackground(Color.cyan);
      ep2a.setForeground(Color.black);
      bs1c.gridx = 2;
      bgbs1.setConstraints(ep2a,bs1c);
      bs1.add(ep2a);
      ep2b = new TextField("",4);
      ep2b.setBackground(Color.cyan);
      ep2b.setForeground(Color.black);
      bs1c.gridx =3;
      bgbs1.setConstraints(ep2b,bs1c);
      bs1.add(ep2b);
      
      bs1c.gridy = 3;
      bs1c.gridx = 0;
      ls3 = new Label(CH + 3);
      bgbs1.setConstraints(ls3,bs1c);
      bs1.add(ls3);
      nr3 = new Choice();
      nr3.add(NORMAL);
      nr3.add(REVERSE);
      bs1c.gridx = 1;
      bgbs1.setConstraints(nr3,bs1c);
      bs1.add(nr3);
      ep3a = new TextField("",4);
      ep3a.setBackground(Color.cyan);
      ep3a.setForeground(Color.black);
      bs1c.gridx = 2;
      bgbs1.setConstraints(ep3a,bs1c);
      bs1.add(ep3a);
      ep3b = new TextField("",4);
      ep3b.setBackground(Color.cyan);
      ep3b.setForeground(Color.black);
      bs1c.gridx =3;
      bgbs1.setConstraints(ep3b,bs1c);
      bs1.add(ep3b);
      
      bs1c.gridy = 4;
      bs1c.gridx = 0;
      ls4 = new Label(CH + 4);
      bgbs1.setConstraints(ls4,bs1c);
      bs1.add(ls4);
      nr4 = new Choice();
      nr4.add(NORMAL);
      nr4.add(REVERSE);
      bs1c.gridx = 1;
      bgbs1.setConstraints(nr4,bs1c);
      bs1.add(nr4);
      ep4a = new TextField("",4);
      ep4a.setBackground(Color.cyan);
      ep4a.setForeground(Color.black);
      bs1c.gridx = 2;
      bgbs1.setConstraints(ep4a,bs1c);
      bs1.add(ep4a);
      ep4b = new TextField("",4);
      ep4b.setBackground(Color.cyan);
      ep4b.setForeground(Color.black);
      bs1c.gridx =3;
      bgbs1.setConstraints(ep4b,bs1c);
      bs1.add(ep4b);
      
      bs1c.gridy = 5;
      bs1c.gridx = 0;
      ls5 = new Label(CH + 5);
      bgbs1.setConstraints(ls5,bs1c);
      bs1.add(ls5);
      nr5 = new Choice();
      nr5.add(NORMAL);
      nr5.add(REVERSE);
      bs1c.gridx = 1;
      bgbs1.setConstraints(nr5,bs1c);
      bs1.add(nr5);
      ep5a = new TextField("",4);
      ep5a.setBackground(Color.cyan);
      ep5a.setForeground(Color.black);
      bs1c.gridx = 2;
      bgbs1.setConstraints(ep5a,bs1c);
      bs1.add(ep5a);
      ep5b = new TextField("",4);
      ep5b.setBackground(Color.cyan);
      ep5b.setForeground(Color.black);
      bs1c.gridx =3;
      bgbs1.setConstraints(ep5b,bs1c);
      bs1.add(ep5b);
      
      bs1c.gridy = 6;
      bs1c.gridx = 0;
      ls6 = new Label(CH + 6);
      bgbs1.setConstraints(ls6,bs1c);
      bs1.add(ls6);
      nr6 = new Choice();
      nr6.add(NORMAL);
      nr6.add(REVERSE);
      bs1c.gridx = 1;
      bgbs1.setConstraints(nr6,bs1c);
      bs1.add(nr6);
      ep6a = new TextField("",4);
      ep6a.setBackground(Color.cyan);
      ep6a.setForeground(Color.black);
      bs1c.gridx = 2;
      bgbs1.setConstraints(ep6a,bs1c);
      bs1.add(ep6a);
      ep6b = new TextField("",4);
      ep6b.setBackground(Color.cyan);
      ep6b.setForeground(Color.black);
      bs1c.gridx =3;
      bgbs1.setConstraints(ep6b,bs1c);
      bs1.add(ep6b);
      
      /* subtrims */
      
      bs1c.gridx = 4;
      bs1c.gridy = 1;
      subt1 = new TextField("",4);
      subt1.setBackground(Color.cyan);
      subt1.setForeground(Color.black);
      bgbs1.setConstraints(subt1,bs1c);
      bs1.add(subt1);

      bs1c.gridy = 2;
      subt2 = new TextField("",4);
      subt2.setBackground(Color.cyan);
      subt2.setForeground(Color.black);
      bgbs1.setConstraints(subt2,bs1c);
      bs1.add(subt2);

      bs1c.gridy = 3;
      subt3 = new TextField("",4);
      subt3.setBackground(Color.cyan);
      subt3.setForeground(Color.black);
      bgbs1.setConstraints(subt3,bs1c);
      bs1.add(subt3);
      
      bs1c.gridy = 4;
      subt4 = new TextField("",4);
      subt4.setBackground(Color.cyan);
      subt4.setForeground(Color.black);
      bgbs1.setConstraints(subt4,bs1c);
      bs1.add(subt4);
      
      bs1c.gridy = 5;
      subt5 = new TextField("",4);
      subt5.setBackground(Color.cyan);
      subt5.setForeground(Color.black);
      bgbs1.setConstraints(subt5,bs1c);
      bs1.add(subt5);
      
      bs1c.gridy = 6;
      subt6 = new TextField("",4);
      subt6.setBackground(Color.cyan);
      subt6.setForeground(Color.black);
      bgbs1.setConstraints(subt6,bs1c);
      bs1.add(subt6);
      
      /* dual rates */

      c.fill = GridBagConstraints.BOTH;
      c.gridwidth  = 1;
      c.gridheight = 3;
      c.weightx = 1;
      c.weighty = 2; //1;
      c.gridx = 3;
      c.gridy = 9;
      bs2 = new BorderPanel(SDATA2);
      gridbag.setConstraints(bs2, c);
      add(bs2);
 
      GridBagConstraints bs2c = new GridBagConstraints();
      GridBagLayout bgbs2 = new GridBagLayout();
      bs2.setLayout(bgbs2);

      bs2c.insets = new Insets(2,8,2,8);

      bs2c.fill = GridBagConstraints.HORIZONTAL;
      bs2c.gridwidth  = 1;
      bs2c.gridheight = 1;
      bs2c.weighty = 0;
      
      bs2c.gridx = 0;
      bs2c.gridy = 1;
      ld1 = new Label(CH + 1);
      bgbs2.setConstraints(ld1,bs2c);
      bs2.add(ld1);
      bs2c.gridx = 1;
      dualon1 = new TextField("",4);
      dualon1.setBackground(Color.cyan);
      dualon1.setForeground(Color.black);
      bgbs2.setConstraints(dualon1,bs2c);
      bs2.add(dualon1);
      bs2c.gridx = 2;
      dualoff1 = new TextField("",4);
      dualoff1.setBackground(Color.cyan);
      dualoff1.setForeground(Color.black);
      bgbs2.setConstraints(dualoff1,bs2c);
      bs2.add(dualoff1);


      bs2c.gridx = 0;
      bs2c.gridy = 2;
      ld2 = new Label(CH + 2);
      bgbs2.setConstraints(ld2,bs2c);
      bs2.add(ld2);
      bs2c.gridx = 1;
      dualon2 = new TextField("",4);
      dualon2.setBackground(Color.cyan);
      dualon2.setForeground(Color.black);
      bgbs2.setConstraints(dualon2,bs2c);
      bs2.add(dualon2);
      bs2c.gridx = 2;
      dualoff2 = new TextField("",4);
      dualoff2.setBackground(Color.cyan);
      dualoff2.setForeground(Color.black);
      bgbs2.setConstraints(dualoff2,bs2c);
      bs2.add(dualoff2);


      bs2c.gridx = 0;
      bs2c.gridy = 3;
      ld3 = new Label(CH + 4);
      bgbs2.setConstraints(ld3,bs2c);
      bs2.add(ld3);
      bs2c.gridx = 1;
      dualon4 = new TextField("",4);
      dualon4.setBackground(Color.cyan);
      dualon4.setForeground(Color.black);
      bgbs2.setConstraints(dualon4,bs2c);
      bs2.add(dualon4);
      bs2c.gridx = 2;
      dualoff4 = new TextField("",4);
      dualoff4.setBackground(Color.cyan);
      dualoff4.setForeground(Color.black);
      bgbs2.setConstraints(dualoff4,bs2c);
      bs2.add(dualoff4);


      /* switches and variables */
      c.fill = GridBagConstraints.BOTH;
      c.gridwidth  = 1;
      c.gridheight = 5;
      c.weightx = 1;
      c.weighty = 3; //1;
      c.gridx = 3;
      c.gridy = 12;
      bs3 = new BorderPanel(SDATA3);
      gridbag.setConstraints(bs3, c);
      add(bs3);
 
      GridBagConstraints bs3c = new GridBagConstraints();
      GridBagLayout bgbs3 = new GridBagLayout();
      bs3.setLayout(bgbs3);

      bs3c.insets = new Insets(2,8,2,8);

      bs3c.fill = GridBagConstraints.HORIZONTAL;
      bs3c.gridwidth  = 1;
      bs3c.gridheight = 1;
      bs3c.weighty = 0;
      
      bs3c.gridx = 0;
      bs3c.gridy = 1;
      lswa = new Label(SWITCH + "A");
      bgbs3.setConstraints(lswa,bs3c);
      bs3.add(lswa);
      
      switcha = new Choice();
      switcha.add(NULL);
      switcha.add(DUAL);
      switcha.add(THROT);
      switcha.add(NORMID);
      bs3c.gridx = 1;
      bgbs3.setConstraints(switcha,bs3c);
      bs3.add(switcha);

      bs3c.gridx = 0;
      bs3c.gridy = 2;
      lswb = new Label(SWITCH + "B");
      bgbs3.setConstraints(lswb,bs3c);
      bs3.add(lswb);
      
      switchb = new Choice();
      switchb.add(NULL);
      switchb.add(DUAL);
      switchb.add(THROT);
      switchb.add(NORMID);
      bs3c.gridx = 1;
      bgbs3.setConstraints(switchb,bs3c);
      bs3.add(switchb);

      bs3c.gridx = 0;
      bs3c.gridy = 3;
      lvara = new Label(VARIABLE + "A");
      bgbs3.setConstraints(lvara,bs3c);
      bs3.add(lvara);
      
      variablea = new Choice();
      variablea.add(NULL);
      variablea.add(ADJUST);
      bs3c.gridx = 1;
      bgbs3.setConstraints(variablea,bs3c);
      bs3.add(variablea);

      bs3c.gridx = 0;
      bs3c.gridy = 4;
      lvarb = new Label(VARIABLE + "B");
      bgbs3.setConstraints(lvarb,bs3c);
      bs3.add(lvarb);
      
      variableb = new Choice();
      variableb.add(NULL);
      variableb.add(ADJUST);
      bs3c.gridx = 1;
      bgbs3.setConstraints(variableb,bs3c);
      bs3.add(variableb);


      /* Mixes */
      c.fill = GridBagConstraints.BOTH;
      c.gridwidth  = 6; 
      c.gridheight = 8;
      c.weightx = 1;
      c.weighty = 2;
      c.gridx = 0;
      c.gridy = 17;
      bs4 = new BorderPanel(SDATA4, SDATA5);
      gridbag.setConstraints(bs4, c);
      add(bs4);
 
      GridBagConstraints bs4c = new GridBagConstraints();
     

      GridBagLayout bgbs4 = new GridBagLayout();
      bs4.setLayout(bgbs4);
      
      bs4c.insets = new Insets(2,8,2,8);

      bs4c.fill = GridBagConstraints.HORIZONTAL;
      bs4c.gridwidth  = 1;
      bs4c.gridheight = 1;
      bs4c.weighty = 0;
      
      bs4c.gridx = 0;
      bs4c.gridy = 0;
      lmsrc = new Label(SOURCE);
      bgbs4.setConstraints(lmsrc,bs4c);
      bs4.add(lmsrc);
      
      bs4c.gridx = 1;
      mix1src = new Choice();
      mix1src.add(CHANNEL + "1");
      mix1src.add(CHANNEL + "2");
      mix1src.add(CHANNEL + "3");
      mix1src.add(CHANNEL + "4");
      mix1src.add(CHANNEL + "5");
      mix1src.add(CHANNEL + "6");
      mix1src.add(VARIABLE + "A");
      mix1src.add(VARIABLE + "B");
      bgbs4.setConstraints(mix1src,bs4c);
      bs4.add(mix1src);
      
      bs4c.gridx = 2;
      mix2src = new Choice();
      mix2src.add(CHANNEL + "1");
      mix2src.add(CHANNEL + "2");
      mix2src.add(CHANNEL + "3");
      mix2src.add(CHANNEL + "4");
      mix2src.add(CHANNEL + "5");
      mix2src.add(CHANNEL + "6");
      mix2src.add(VARIABLE + "A");
      mix2src.add(VARIABLE + "B");
      bgbs4.setConstraints(mix2src,bs4c);
      bs4.add(mix2src);
      
      bs4c.gridx = 3;
      mix3src = new Choice();
      mix3src.add(CHANNEL + "1");
      mix3src.add(CHANNEL + "2");
      mix3src.add(CHANNEL + "3");
      mix3src.add(CHANNEL + "4");
      mix3src.add(CHANNEL + "5");
      mix3src.add(CHANNEL + "6");
      mix3src.add(VARIABLE + "A");
      mix3src.add(VARIABLE + "B");
      bgbs4.setConstraints(mix3src,bs4c);
      bs4.add(mix3src);
      
      bs4c.gridx = 0;
      bs4c.gridy = 1;
      lmdest = new Label(DEST);
      bgbs4.setConstraints(lmdest,bs4c);
      bs4.add(lmdest);
      
      bs4c.gridx = 1;
      mix1dest = new Choice();
      mix1dest.add(CHANNEL + "1");
      mix1dest.add(CHANNEL + "2");
      mix1dest.add(CHANNEL + "3");
      mix1dest.add(CHANNEL + "4");
      mix1dest.add(CHANNEL + "5");
      mix1dest.add(CHANNEL + "6");
      bgbs4.setConstraints(mix1dest,bs4c);
      bs4.add(mix1dest);      

      bs4c.gridx = 2;
      mix2dest = new Choice();
      mix2dest.add(CHANNEL + "1");
      mix2dest.add(CHANNEL + "2");
      mix2dest.add(CHANNEL + "3");
      mix2dest.add(CHANNEL + "4");
      mix2dest.add(CHANNEL + "5");
      mix2dest.add(CHANNEL + "6");
      bgbs4.setConstraints(mix2dest,bs4c);
      bs4.add(mix2dest);      

      bs4c.gridx = 3;
      mix3dest = new Choice();
      mix3dest.add(CHANNEL + "1");
      mix3dest.add(CHANNEL + "2");
      mix3dest.add(CHANNEL + "3");
      mix3dest.add(CHANNEL + "4");
      mix3dest.add(CHANNEL + "5");
      mix3dest.add(CHANNEL + "6");
      bgbs4.setConstraints(mix3dest,bs4c);
      bs4.add(mix3dest);      

      bs4c.gridx = 0;
      bs4c.gridy = 2;
      lmup = new Label(UP);
      bgbs4.setConstraints(lmup,bs4c);
      bs4.add(lmup);

      bs4c.gridx = 1;
      mix1up = new TextField("",4);
      mix1up.setBackground(Color.cyan);
      mix1up.setForeground(Color.black);
      bgbs4.setConstraints(mix1up,bs4c);
      bs4.add(mix1up);

      bs4c.gridx = 2;
      mix2up = new TextField("",4);
      mix2up.setBackground(Color.cyan);
      mix2up.setForeground(Color.black);
      bgbs4.setConstraints(mix2up,bs4c);
      bs4.add(mix2up);

      bs4c.gridx = 3;
      mix3up = new TextField("",4);
      mix3up.setBackground(Color.cyan);
      mix3up.setForeground(Color.black);
      bgbs4.setConstraints(mix3up,bs4c);
      bs4.add(mix3up);

      bs4c.gridx = 0;
      bs4c.gridy = 3;
      lmdown = new Label(DOWN);
      bgbs4.setConstraints(lmdown,bs4c);
      bs4.add(lmdown);
      
      bs4c.gridx = 1;
      mix1down = new TextField("",4);
      mix1down.setBackground(Color.cyan);
      mix1down.setForeground(Color.black);
      bgbs4.setConstraints(mix1down,bs4c);
      bs4.add(mix1down);

      bs4c.gridx = 2;
      mix2down = new TextField("",4);
      mix2down.setBackground(Color.cyan);
      mix2down.setForeground(Color.black);
      bgbs4.setConstraints(mix2down,bs4c);
      bs4.add(mix2down);

      bs4c.gridx = 3;
      mix3down = new TextField("",4);
      mix3down.setBackground(Color.cyan);
      mix3down.setForeground(Color.black);
      bgbs4.setConstraints(mix3down,bs4c);
      bs4.add(mix3down);

      
      bs4c.gridx = 0;
      bs4c.gridy = 4;
      lmswitch = new Label(SWITCH);
      bgbs4.setConstraints(lmswitch,bs4c);
      bs4.add(lmswitch);

      bs4c.gridx = 1;
      mix1sw = new Choice();
      mix1sw.add(SWITCH + "A");
      mix1sw.add(SWITCH + "B");
      mix1sw.add(ON);
      mix1sw.add(OFF);
      bgbs4.setConstraints(mix1sw,bs4c);
      bs4.add(mix1sw);      

      bs4c.gridx = 2;
      mix2sw = new Choice();
      mix2sw.add(SWITCH + "A");
      mix2sw.add(SWITCH + "B");
      mix2sw.add(ON);
      mix2sw.add(OFF);
      bgbs4.setConstraints(mix2sw,bs4c);
      bs4.add(mix2sw);      

      bs4c.gridx = 3;
      mix3sw = new Choice();
      mix3sw.add(SWITCH + "A");
      mix3sw.add(SWITCH + "B");
      mix3sw.add(ON);
      mix3sw.add(OFF);
      bgbs4.setConstraints(mix3sw,bs4c);
      bs4.add(mix3sw);    
   
      /* mode and stick */
      c.fill = GridBagConstraints.NONE;
      c.gridwidth = 2;
      c.weighty = 0; //1;
      c.gridheight = 1;
      c.weightx = 1;
      c.gridx = 4;
      c.gridy = 9; //9;
      lm = new Label(MODE);
      gridbag.setConstraints(lm,c);
      add(lm);
      
      c.gridy = 10;
      c. gridx = 4;
      mode = new Choice();
      mode.add(ACRO);
      mode.add(HELI120);
      mode.add(HELI90);
      mode.add(HELI140);
      gridbag.setConstraints(mode,c);
      add(mode);
      mode.addItemListener(this);
   
      c.gridx = 4;
      lsk = new Label(STICK);
      c.gridy = 11;
      gridbag.setConstraints(lsk,c);
      add(lsk);
      
      stick = new Choice();
      stick.add(MODE + "1");
      stick.add(MODE + "2");
      stick.add(MODE + "3");
      stick.add(MODE + "4");
      c. gridx = 4;
      c.gridy = 12; //12;
      gridbag.setConstraints(stick,c);
      add(stick);  

      /* LEDs */
      c.fill = GridBagConstraints.NONE;
      c.gridwidth = 1;
      c.weighty = 1;
      c.gridheight = 1;
          
      lc = new Label(CONNECTED);
      c.weightx = 1;
      c.gridx = 4; //5;
      c.gridy = 13; //13;
      gridbag.setConstraints(lc,c);
      add(lc);
      connected = new LEDCanvas(Color.red);
 
      c. gridx = 5;
      c.gridy = 13; //14;
      gridbag.setConstraints(connected, c);
      add(connected);
      
      c. gridx = 4;
      c.gridy = 14; //15;
      ls = new Label(SYNCED);
      gridbag.setConstraints(ls,c);
      add(ls);
      synced = new LEDCanvas(Color.red);

      c. gridx = 5;
      c.gridy = 14; //16;
      gridbag.setConstraints(synced, c);
      add(synced);

      c.gridx = 4;
      c.gridy = 15;
      c.gridwidth = 2;
      ln = new Label(LOADFRM);
      gridbag.setConstraints(ln,c);
      add(ln);

      c.gridx = 4;
      c.gridy = 16;
      c.gridwidth = 2;
      fn = new TextField(" ",20);
      fn.setBackground(Color.cyan);
      fn.setForeground(Color.black);
      fn.setEnabled(false);
      gridbag.setConstraints(fn,c);
      add(fn);
       

      /* message area */
      c.gridx = 0;
      c.gridy = 25;
      c.gridwidth = 6;
      c.fill = GridBagConstraints.HORIZONTAL;
      message = new TextField("",60);
      message.setBackground(Color.cyan);
      message.setForeground(Color.black);
      gridbag.setConstraints(message, c);
      add(message);
      
      /* buttons */
      c.fill = GridBagConstraints.NONE;
      c.gridwidth = 1;
      c.weighty = 0;
      c.gridheight = 1;
      
      get = new Button(GET);
      get.setEnabled(false);
      get.addActionListener(this);
      c.gridx = 0;
      c.gridy = 26;
      gridbag.setConstraints(get,c);
      add(get);

      send = new Button(SEND);
      send.setEnabled(false);
      send.addActionListener(this);
      c.gridx = 1;
      gridbag.setConstraints(send,c);
      add(send);

      save = new Button(SAVE);
      save.setEnabled(false);
      save.addActionListener(this);
      c.gridx = 2;
      gridbag.setConstraints(save,c);
      add(save);

      restore = new Button(RESTORE);
      restore.addActionListener(this);
      c.gridx = 3;
      gridbag.setConstraints(restore,c);
      add(restore);

      calibrate = new Button(CALIBRATE);
      calibrate.setEnabled(false);
      calibrate.addActionListener(this);
      c.gridx = 4;
      gridbag.setConstraints(calibrate,c);
      add(calibrate);

      exit = new Button(EXIT);
      exit.addActionListener(this);
      c.gridx = 5;
      gridbag.setConstraints(exit,c);
      add(exit);
      
      String s = getProperty(HPROP);
      hprop = getInt(s);
      if (hprop < 100)
         hprop = DEFH;
      s = getProperty(WPROP);
      wprop = getInt(s);
      if (wprop < 100)
         wprop = DEFW;
         
      s = getProperty(PPROP);
      if (s.length() > 0)
         portname = s;
      else 
         portname = DEFPORT;
      portname2 = SIPORT;
         
      s = getProperty(LPROP);
      if (s.length() > 0)
         libname = s;
      else 
         libname = DEFLIB;
  
      setSize(wprop, hprop);
      setLocation(50,50); 
      setVisible(true);

      te = new TurborixEngine(this, portname, portname2, libname);
      te.start();
      hc = new HeliConfig(this, te);
      cdlg = new Calibrate(this, te);

   }

   public void setMessage(String t) {
      message.setText(t);
      if (logging) 
         addLogMsg(t);
   }
   
   public void notify(int c, int d) {
      if (!cok) {
         cok = true;
         get.setEnabled(true);
         connected.setColor(Color.green);
      }
      d = (d*200)/1024 - 100;
      if (c==1) {
         ch1.setText("" + d);
         hchscroll1.setValue(d);
      } else if (c==2) {
         ch2.setText("" + d);
         hchscroll2.setValue(d);
      } else if (c==3) {
         ch3.setText("" + d);
         hchscroll3.setValue(d);
      } else if (c==4) {
         ch4.setText("" + d);
         hchscroll4.setValue(d);
      } else if (c==5) {
         ch5.setText("" + d);
         hchscroll5.setValue(d);
      } else if (c==6) {
         ch6.setText("" + d);
         hchscroll6.setValue(d);
      }
   }
   
   public int validateChoice(int num, Choice c, int v, int n) {
      if (v < 0 || v >= n) {
         addLogMsg("Choice " + num + " value " + v + " out of range " + 0 + ":" + n);
         return 0;
      } else {
         return v;
      }
   }
   
   public void validateChoices(SettingData sdata) {
           mode.select(validateChoice(1,mode,sdata.model, 4));
          stick.select(validateChoice(2,stick,sdata.stick, 4));
            nr1.select(validateChoice(3,nr1,sdata.reverse[0], 2));
            nr2.select(validateChoice(4,nr2,sdata.reverse[1], 2));
            nr3.select(validateChoice(5,nr3,sdata.reverse[2], 2));
            nr4.select(validateChoice(6,nr4,sdata.reverse[3], 2));
            nr5.select(validateChoice(7,nr5,sdata.reverse[4], 2));
            nr6.select(validateChoice(8,nr6,sdata.reverse[5], 2));
        switcha.select(validateChoice(9,switcha,sdata.switchA, 4));
      variablea.select(validateChoice(10,variablea,sdata.variableA, 2));
        switchb.select(validateChoice(11,switchb,sdata.switchB, 4));
      variableb.select(validateChoice(12,variableb,sdata.variableB, 2));
        mix1src.select(validateChoice(13,mix1src,sdata.mixer[0][0], 8));
        mix2src.select(validateChoice(14,mix2src,sdata.mixer[1][0], 8));
        mix3src.select(validateChoice(15,mix3src,sdata.mixer[2][0], 8));
       mix1dest.select(validateChoice(16,mix1dest,sdata.mixer[0][1], 6));
       mix2dest.select(validateChoice(17,mix2dest,sdata.mixer[1][1], 6));
       mix3dest.select(validateChoice(18,mix3dest,sdata.mixer[2][1], 6));
         mix1sw.select(validateChoice(19,mix1sw,sdata.mixer[0][4], 4));
         mix2sw.select(validateChoice(20,mix2sw,sdata.mixer[1][4], 4));
         mix3sw.select(validateChoice(21,mix3sw,sdata.mixer[2][4], 4));
   }
   
   public void applySettings(SettingData sdata) {
      save.setEnabled(true);
      oksd = sdata;
     
      hc.setVisible(sdata.model != 0);
      
      validateChoices(sdata);      
      ep1a.setText("" + sdata.end[0][0]);
      ep1b.setText("" + sdata.end[0][1]);
      ep2a.setText("" + sdata.end[1][0]);
      ep2b.setText("" + sdata.end[1][1]);
      ep3a.setText("" + sdata.end[2][0]);
      ep3b.setText("" + sdata.end[2][1]);
      ep4a.setText("" + sdata.end[3][0]);
      ep4b.setText("" + sdata.end[3][1]);
      ep5a.setText("" + sdata.end[4][0]);
      ep5b.setText("" + sdata.end[4][1]);
      ep6a.setText("" + sdata.end[5][0]);
      ep6b.setText("" + sdata.end[5][1]);

      subt1.setText("" + sdata.subtrim[0]);
      subt2.setText("" + sdata.subtrim[1]);
      subt3.setText("" + sdata.subtrim[2]);
      subt4.setText("" + sdata.subtrim[3]);
      subt5.setText("" + sdata.subtrim[4]);
      subt6.setText("" + sdata.subtrim[5]);
      
      dualon1.setText("" + sdata.dualon[0]);
      dualon2.setText("" + sdata.dualon[1]);
      dualon4.setText("" + sdata.dualon[2]);
      dualoff1.setText("" + sdata.dualoff[0]);
      dualoff2.setText("" + sdata.dualoff[1]);
      dualoff4.setText("" + sdata.dualoff[2]);
      
      mix1up.setText(""+sdata.mixer[0][2]);
      mix2up.setText(""+sdata.mixer[1][2]);
      mix3up.setText(""+sdata.mixer[2][2]);
      mix1down.setText(""+sdata.mixer[0][3]);
      mix2down.setText(""+sdata.mixer[1][3]);
      mix3down.setText(""+sdata.mixer[2][3]);
      validateFields();
   }

   public void notifySettings(SettingData sdata) {
       if (!sok) {
         sok = true;
         send.setEnabled(true);
         calibrate.setEnabled(true);      
         synced.setColor(Color.green);
      }
      applySettings(sdata);  
      hc.applySettings(sdata); 
      if (sdata.model != SettingData.ACRO) {
        hc.setVisible(true);
      }
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
         addLogMsg("Field " + t + " value " + i + " out of range " + lo + ":" + hi);
      return e;
   }
   
   public int validateFields() {
      int j;

      j = 0;
      j += validateField(ep1a,0,120);
      j += validateField(ep1b,0,120);
      j += validateField(ep2a,0,120);
      j += validateField(ep2b,0,120);
      j += validateField(ep3a,0,120);
      j += validateField(ep3b,0,120);
      j += validateField(ep4a,0,120);
      j += validateField(ep4b,0,120);
      j += validateField(ep5a,0,120);
      j += validateField(ep5b,0,120);
      j += validateField(ep6a,0,120);
      j += validateField(ep6b,0,120);
      j += validateField(dualon1,0,100);
      j += validateField(dualon2,0,100);
      j += validateField(dualon4,0,100);
      j += validateField(dualoff1,0,100);
      j += validateField(dualoff2,0,100);
      j += validateField(dualoff4,0,100);
      j += validateField(subt1,-120,120);
      j += validateField(subt2,-120,120);
      j += validateField(subt3,-120,120);
      j += validateField(subt4,-120,120);
      j += validateField(subt5,-120,120);
      j += validateField(subt6,-120,120);
      j += validateField(mix1up,-100,100);
      j += validateField(mix2up,-100,100);
      j += validateField(mix3up,-100,100);
      j += validateField(mix1down,-100,100);
      j += validateField(mix2down,-100,100);
      j += validateField(mix3down,-100,100);
      return j;
   }
   
   public int[] buildSettings() {
      int i,j, k;
      String s;
      int[] sd, osd;
      int chksum = 0;
      
      if (validateFields() != 0) {
         setMessage("" + validateFields() + " value(s) invalid");
         return null;
      }
 
      osd = te.getLatestSettingData();
      sd = new int[te.SETTINGLEN];
      for (i=0; i<te.SETTINGLEN; i++) 
         sd[i] = osd[i];
      
      // sd[0] is mode and stick
      sd[0] = mode.getSelectedIndex() + (stick.getSelectedIndex()<<4); // was <<4 and null
      // sd[1] is reverse bits
      j = 0;
      i = 0;
      j  = nr1.getSelectedIndex();    
      i++;
      j += nr2.getSelectedIndex() << i; 
      i++;   
      j += nr3.getSelectedIndex() << i; 
      i++;   
      j += nr4.getSelectedIndex() << i;
      i++;    
      j += nr5.getSelectedIndex() << i; 
      i++;   
      j += nr6.getSelectedIndex() << i;    
      sd[1] = j;
      
      // sd[2-7] = dualonoffs
      s = dualon1.getText();
      sd[2] = getInt(s);
      s = dualoff1.getText();
      sd[3] = getInt(s);
      s = dualon2.getText();
      sd[4] = getInt(s);
      s = dualoff2.getText();
      sd[5] = getInt(s);
      s = dualon4.getText();
      sd[6] = getInt(s);
      s = dualoff4.getText();
      sd[7] = getInt(s);
      
      // sd[8-10] heli stuff - leave as-is
      
      // sd[11-22] = ends */
      s = ep1a.getText();
      sd[11] = getInt(s);
      s = ep1b.getText();
      sd[12] = getInt(s);
      s = ep2a.getText();
      sd[13] = getInt(s);
      s = ep2b.getText();
      sd[14] = getInt(s);
      s = ep3a.getText();
      sd[15] = getInt(s);
      s = ep3b.getText();
      sd[16] = getInt(s);
      s = ep4a.getText();
      sd[17] = getInt(s);
      s = ep4b.getText();
      sd[18] = getInt(s);
      s = ep5a.getText();
      sd[19] = getInt(s);
      s = ep5b.getText();
      sd[20] = getInt(s);
      s = ep6a.getText();
      sd[21] = getInt(s);
      s = ep6b.getText();
      sd[22] = getInt(s);
      
      // sd[23-42] more heli stuff - leave as-is
      
      // sd[43-48] subtrims (2's complement)
      s = subt1.getText();
      i = getInt(s) ;
      sd[43] = (i < 0) ? i+256 : i;
      s = subt2.getText();
      i = getInt(s) ;
      sd[44] = (i < 0) ? i+256 : i;
      s = subt3.getText();
      i = getInt(s) ;
      sd[45] = (i < 0) ? i+256 : i;
      s = subt4.getText();
      i = getInt(s) ;
      sd[46] = (i < 0) ? i+256 : i;
      s = subt5.getText();
      i = getInt(s) ;
      sd[47] = (i < 0) ? i+256 : i;
      s = subt6.getText();
      i = getInt(s) ;
      sd[48] = (i < 0) ? i+256 : i;
      
      // sd[49-60] mixers
      sd[49] = (mix1src.getSelectedIndex()<<4) + mix1dest.getSelectedIndex();
      s = mix1up.getText();
      i = getInt(s) ;
      sd[50] = (i < 0) ? i+256 : i;
      s = mix1down.getText();
      i = getInt(s) ;
      sd[51] = (i < 0) ? i+256 : i;
      sd[52] = mix1sw.getSelectedIndex();

      sd[53] = (mix2src.getSelectedIndex()<<4) + mix2dest.getSelectedIndex();
      s = mix2up.getText();
      i = getInt(s) ;
      sd[54] = (i < 0) ? i+256 : i;
      s = mix2down.getText();
      i = getInt(s) ;
      sd[55] = (i < 0) ? i+256 : i;
      sd[56] = mix2sw.getSelectedIndex();
 
      sd[57] = (mix3src.getSelectedIndex()<<4) + mix3dest.getSelectedIndex();
      s = mix3up.getText();
      i = getInt(s) ;
      sd[58] = (i < 0) ? i+256 : i;
      s = mix3down.getText();
      i = getInt(s) ;
      sd[59] = (i < 0) ? i+256 : i;
      sd[60] = mix3sw.getSelectedIndex();
     
    
      sd[61] = switcha.getSelectedIndex();  
      sd[62] = switchb.getSelectedIndex();  
      sd[63] = variablea.getSelectedIndex();  
      sd[64] = variableb.getSelectedIndex();  
      
      // if heli config then build heli settings
      hc.buildSettings(sd);
      
      // compute checksum
      for (i=0; i<65;i++) {  
         chksum += sd[i];
      }
      sd[65] = chksum/256;
      sd[66] = chksum%256;
      return sd;
   }
   
   public int getInt(String s) {
      try {
         int i = Integer.parseInt(s);
         return i;
      } catch (Exception e) {
         return -999;
      }
   }
 
   public void openLog(String fname) {
      FileOutputStream out = null;
      BufferedOutputStream bos = null;
      logOut = null;
     
      try {
         out = new FileOutputStream(fname);
         bos = new BufferedOutputStream(out);
         logOut = new DataOutputStream(bos);
      } catch(IOException e) {
         setMessage("Error opening log file " + fname + " " + e);
      }        
   }
   
   public void closeLog() {
      try {
         logOut.close();
      } catch (Exception e) {
         setMessage("Error closing log file " + e);
      }
   }
   
/*----------------------------------------------------------------------------*/
/*  write an error message to the log                                         */
/*----------------------------------------------------------------------------*/
   public void write(String filetxt) {

      try {
         logOut.writeBytes(filetxt);
         logOut.flush();
      } catch(IOException e) {
         setMessage("Error in writing to log file " + e);
      }
   }
   
   
   public void addLogMsg(String s) {
      if (logging)
         write(s + CRLF);
   }

/*----------------------------------------------------------------------------*/
/*  handle a GUI action event                                                 */
/*----------------------------------------------------------------------------*/
   public void actionPerformed(ActionEvent event) {
      boolean ok;
      int[] sd;
      String fname;
      Object o = event.getSource();
      if (o == exit) {
          te.stop();
          doClose();
      } else if (o == get) {
          te.getSettingsData();
          ln.setText(LOADFRM);
          fn.setText(TRANSMIT);
      } else if (o == send) {
         setMessage("");
         sd = buildSettings();
         if (sd != null) {
            ok = te.sendSettingsData(sd);
            if (ok)
               setMessage("Settings transmitted successfully");
            else 
               setMessage("Send failure");
          } else {
             setMessage("data invalid - send cancelled");
          }
            
      } else if (o == save) {
         setMessage("");
         try {
            sd = buildSettings(); 
            if (sd == null) {
               setMessage("data is invalid");
               return;
            } 
            filedialog.setTitle(SAVEDLG);    
            filedialog.setMode(FileDialog.SAVE);  
            filedialog.setFile("*.trb");
            filedialog.setDirectory(folder);
            filedialog.setVisible(true);
            String filename = filedialog.getFile();
            if (filename != null) {
               folder = filedialog.getDirectory();
               fname = folder + filename;
               ok = te.saveSettingsData(sd, fname);
               if (ok) {
                  setMessage("Settings saved");
                  ln.setText(SAVEDAS);
                  fn.setText(filename);
               }   
            }
         } catch (Exception e) {
            setMessage("Error obtaining savefile name");
         }

      } else if (o == restore) {
         setMessage("");
         try {
            filedialog.setTitle(RESTDLG);    
            filedialog.setMode(FileDialog.LOAD);  
            filedialog.setFile("*.trb");
            filedialog.setDirectory(folder);
            filedialog.setVisible(true);
            String filename = filedialog.getFile();
            if (filename != null) {
               folder = filedialog.getDirectory();
               fname = folder + filename;
               sd = te.loadSettingsData(fname);
                   SettingData sdata = new SettingData(sd);
               if (sdata != null) {
                  if (sdata.valid) {
                     applySettings(sdata);
                     hc.applySettings(sdata);
                     ln.setText(LOADFRM);
                     fn.setText(filename);
                  } else {
                      setMessage("Bad checksum");
                  }

               } else {
                  setMessage("Error in data");  
               }

            }
         } catch (Exception e) {
            setMessage("Error obtaining restore file name " + e);
         }

      } else if (o == calibrate) {
         setMessage("Calibrating");
         cdlg.setVisible(true);

      }
   }

/*----------------------------------------------------------------------------*/
/*  ignore most GUI item events                                               */
/*----------------------------------------------------------------------------*/
   public void itemStateChanged(ItemEvent event) {
      Object o = event.getSource();
      if (o == mode) {
         hc.setVisible(mode.getSelectedIndex() != 0);
      }
   }  
   public void windowClosed(WindowEvent event) {
   }
   public void windowDeiconified(WindowEvent event) {
   }
   public void windowIconified(WindowEvent event) {
   }
   public void windowActivated(WindowEvent event) {
   }
   public void windowDeactivated(WindowEvent event) {
   }
   public void windowOpened(WindowEvent event) {
   }

/*----------------------------------------------------------------------------*/
/*  load the given properties file                                            */
/*----------------------------------------------------------------------------*/
   void loadProperties(String propfile) {
      try {
         FileInputStream in = new FileInputStream(propfile);
         BufferedInputStream bis = new BufferedInputStream(in);
         props.load(bis);
         in.close();
         loaded = true;
      } catch (Exception e) {
      }
   }
   
/*----------------------------------------------------------------------------*/
/*  get a named property from the properties file                             */
/*----------------------------------------------------------------------------*/
   public String getProperty(String prop) {
      if (!loaded)
         loadProperties(PROPS);
      String s = (String)props.getProperty(prop);
      if (s == null)
         s = "";
      return s;
   }
   
/*----------------------------------------------------------------------------*/
/*  put a named property into the properties file                             */
/*----------------------------------------------------------------------------*/
   public void putProperty(String prop, String value) {
      props.put(prop, value);
   }

/*----------------------------------------------------------------------------*/
/*  save the main properties file                                             */
/*----------------------------------------------------------------------------*/
   boolean saveProperties(String propfile) {
      try {
         hprop = getHeight();
         wprop = getWidth();
         putProperty(HPROP, "" + hprop);
         putProperty(WPROP, "" + wprop);
         putProperty(FLDPROP, folder);
         FileOutputStream out = new FileOutputStream(propfile);
         BufferedOutputStream bos = new BufferedOutputStream(out);
         props.store(bos, PROPT);
         bos.flush();
         bos.close();
         return true;
      } catch (Exception e) {
      }
      return false;
   }

/*----------------------------------------------------------------------------*/
/*  we're being shut down, so save the current settings                       */
/*----------------------------------------------------------------------------*/
   public void windowClosing(WindowEvent event) {
      closeLog();
      doClose();
   }
   
   public void doClose() {
      saveProperties(PROPS);
      System.exit(0);
   }

   public static void main (String[] args) {
      new TurborixConfig();
   }
}
