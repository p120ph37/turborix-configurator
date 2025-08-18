package com.zenoshrdlu.turborix;

/******************************************************************************
*                                                                             *
* Engine for Turborix Configurator                                            *
* Dave Mitchell dave@zenoshrdlu.com                                           *
*                                                                             *
* Updated to use jSerialComm instead of RXTX                                  *
*                                                                             *
******************************************************************************/

import com.fazecast.jSerialComm.SerialPort;
import java.io.*;

public class TurborixEngine extends Object implements Runnable {
   public static final int MAGIC  = 85;
   public static final int PUSHID = 252;
   public static final int GETID  = 253;
   public static final int SENDID = 250;
   public static final int UPDTID = 255;
   
   public static final int STARTC = 153;
   public static final int STOPC  = 136;
   
   public static final int DATALEN    = 16;
   public static final int SETTINGLEN = 67;
   public static final int BAUDRATE = 115200;
   public static final int PORTID   = 2000;
   
   private TurborixUI tui;
   private Thread runner   = null;
   private boolean running = false;
   private ChannelData oldcd;
   private ChannelData newcd;
   private int[] latestsd = null;
   private InputStream in;
   private OutputStream out;
   private boolean connected = false;
   private boolean synced = false;
   private boolean okport = false;
   private boolean testget = false;
   private boolean portok = false;
   private int done = 0;
   private int hex = 0;
   private String hexstr = "";
   
   
   public boolean isConnected() {
      return connected;
   }
     
   public TurborixEngine(TurborixUI t, String portname, String altportname) {
      super();
      tui = t;
      newcd = new ChannelData();
      oldcd = new ChannelData();
      try {
         tui.setMessage("Trying to open '" + portname + "'");
         connect(portname);
         portok = true;
         tui.setMessage("Connected to " + portname);
      } catch ( Exception e ) {
         tui.setMessage("Connect to '" + portname + "' failed - " + e.getMessage());
      }
      if (!portok) {
          try {
             tui.setMessage("Trying to open '" + altportname + "'");
             connect(altportname);
             portok = true;
             tui.setMessage("Connected to " + altportname);
          } catch (Exception e1) {
             tui.setMessage("Connect '" + portname + "' and '" + altportname + "' failed - " + e1.getMessage());
         }
      }
   }

   public void start() {
      if (!okport)
         return;
      if (runner != null)
         return;
      running = true;
      tui.addLogMsg("starting data thread");
      runner = new Thread(this);
      runner.start();
   }

   public void stop() {
      if (runner == null)
         return;
      running = false;
      runner = null;
   }

   public void run() {
      int i, j;
      while (running) {
         try {
            Thread.sleep(10);
            newcd = fillChannelData();
            if (newcd.valid) {
               for (i = 1; i < 7; i++) { // 6 ints = 12 bytes of data
                  j = (newcd.ch[i] - oldcd.ch[i]);
                  if (j != 0)
                     tui.notify(i, newcd.ch[i]);
                  oldcd.ch[i] = newcd.ch[i];
               }
               if (!testget) { // try to get current settings
                  testget = true;
                  getSettingsData();
               }
            }
         } catch(Exception e) {
            tui.addLogMsg("Error in data thread: " + e);
            System.exit(0);
         }
      }
   }

   void connect ( String portName ) throws Exception {
      SerialPort serialPort = SerialPort.getCommPort(portName);
      serialPort.setComPortParameters(BAUDRATE, 8, SerialPort.ONE_STOP_BIT, SerialPort.NO_PARITY);
      serialPort.setComPortTimeouts(SerialPort.TIMEOUT_READ_BLOCKING, 0, 0);
      serialPort.openPort();
      in = serialPort.getInputStream();
      out = serialPort.getOutputStream();
      okport = true;
   }
   
   public int[] getLatestSettingData() {
      return latestsd;
   }
   
   public int[] loadSettingsData(String filename) {
      int i;
      int[] sd;
      byte[] sdb;
      sd = new int[67];
      sdb = new byte[67];
      try {
         InputStream ins = new FileInputStream(filename);
         DataInputStream dis = new DataInputStream(ins);
         dis.read(sdb);
         dis.close();
         testget = true; // prevent restored settings being overwritten
         for (i=0; i<67;i++)
            sd[i] = sdb[i] & 0xff;
            latestsd = sd;               
         return sd;
      } catch(Exception e) {
         tui.setMessage("Load Error " + e);
      }
      return null;
   }
   
   public boolean saveSettingsData(int[] sd, String fname) {
      int i;

      byte[] sdb = new byte[sd.length];
      for (i=0; i<sd.length;i++)
         sdb[i] = (byte)sd[i];
      
      FileOutputStream out = null;
      BufferedOutputStream bos = null;
      DataOutputStream dataOut = null;
      try {
         out = new FileOutputStream(fname);
         bos = new BufferedOutputStream(out);
         dataOut = new DataOutputStream(bos);
      } catch(IOException e) {
         tui.setMessage("Error opening file " + fname);
         return false;
      }

      try {
         dataOut.write(sdb,0, sdb.length);
         dataOut.flush();
         out.close();
      } catch(IOException e) {
         tui.setMessage("Error in writing to file " + fname);
         return false;
      }
      return true;
   }

   public boolean sendSettingsData(int[] sd) {
      int i;
      byte b;
      try {
         out.write(0);
         out.write(MAGIC);
         out.write(UPDTID);
         for (i=0; i<SETTINGLEN;i++) {
            b = (byte)sd[i];
            out.write(b);
         }
         out.write(0);
         return true;
      } catch (Exception e) {
        tui.addLogMsg("Send failed - " + e);
      }
      return false;   
   }
   
   public void startCalibrate() {
      try {
         out.write(0);
         out.write(MAGIC);
         out.write(STARTC);
         out.write(0);
      } catch (Exception e) {
         tui.addLogMsg("Start calibrate failed - " + e);
      }
   }
   
   public void stopCalibrate() {
      try {
         out.write(0);
         out.write(MAGIC);
         out.write(STOPC);
         out.write(0);
      } catch (Exception e) {
         tui.addLogMsg("Stop calibrate failed - " + e);
      }
   }
      
   public void getSettingsData() {
      try {
         out.write(0);
         out.write(MAGIC);
         out.write(SENDID);
         out.write(0);
      } catch (Exception e) {
         tui.addLogMsg("Get failed - " + e);
      }
   }
   
   public ChannelData fillChannelData() {
      int i, k, m;
      int[] cdata;
      int[] sdata;
      cdata = new int[DATALEN];
      sdata = new int[SETTINGLEN];
      ChannelData cd = null;
      SettingData sd = null;
      if (done == 0)
         tui.addLogMsg("starting fillChannelData");
      try {
            while (true) {
                    i = in.read();
                    connected = true;
                    if (i == MAGIC){
                        if (done == 0) { 
                           done = 1;
                           tui.addLogMsg("MAGIC read");
                        }   
                        i = in.read();
                        if (i == PUSHID) { 
                           for (k=0; k<DATALEN; k++)
                              cdata[k] = in.read();
                           cd = new ChannelData(cdata); 
                           if (done < 2) {
                              done = 2;
                              tui.addLogMsg("PUSHID read");
                           }  
                           return cd;
                        } else if (i == GETID) {
                           synced = true;
                           for (k=0; k<SETTINGLEN; k++)
                               sdata[k] = in.read();
                           latestsd = sdata;   
                           sd = new SettingData(sdata); 
                           if (done < 3) {
                               done = 3;
                               tui.addLogMsg("GETID read");
                               tui.addLogMsg("notifying settings");
                           }  
                           tui.notifySettings(sd);  
                        } else {
                           tui.addLogMsg("read but not PUSHID or GETID");
                        }   
                    } else {
                        if (hex < 100) {
//                       tui.addLogMsg("read but not MAGIC");
                           hexstr = hexstr + Integer.toHexString(i);
                           hex++;
                        } else if (hex == 100) {
                           tui.addLogMsg("nonmagic=" + hexstr);
                           hex++;
                        }
                    }
            }     
      } catch ( IOException e ) {
         connected = false;
         tui.setMessage("fillChannelData - data error - " + e);
         e.printStackTrace();
      } 
      return cd;
   } 
                                         
}
