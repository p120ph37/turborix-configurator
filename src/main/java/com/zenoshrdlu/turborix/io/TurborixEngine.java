package com.zenoshrdlu.turborix.io;

/******************************************************************************
*                                                                             *
* Engine for Turborix Configurator                                            *
* Dave Mitchell dave@zenoshrdlu.com                                           *
*                                                                             *
* Updated to use jSerialComm instead of RXTX                                  *
*                                                                             *
******************************************************************************/

import com.fazecast.jSerialComm.SerialPort;
import com.zenoshrdlu.turborix.ChannelData;
import com.zenoshrdlu.turborix.TurborixConfig;
import com.zenoshrdlu.turborix.settings.SettingData;

import java.io.*;

public class TurborixEngine extends Object implements Runnable {
    public static final int MAGIC  = 85;
    public static final int PUSHID = 252;
    public static final int GETID  = 253;
    public static final int SENDID = 250;
    public static final int UPDTID = 255;

    public static final int STARTC = 153;
    public static final int STOPC  = 136;

    public static final int SETTINGLEN = 67;
    public static final int BAUDRATE = 115200;

    private TurborixConfig tui;
    private Thread runner   = null;
    private boolean running = false;
    private ChannelData oldcd = new ChannelData();
    private byte[] latestsd = null;
    private InputStream in;
    private OutputStream out;
    private boolean connected = false;
    private boolean okport = false;
    private boolean testget = false;
    private boolean portok = false;
    private int done = 0;
    private int hex = 0;
    private String hexstr = "";
    private String connectport = null;

    public String getConnectPort() {
        return connectport;
    }

    public boolean isConnected() {
        return connected;
    }

    public TurborixEngine(TurborixConfig t, String... portnames) {
        super();
        tui = t;
        for (String portname : portnames) {
            try {
                tui.setMessage("Trying to open '" + portname + "'");
                connect(portname);
                portok = true;
                tui.setMessage("Connected to " + portname);
                connectport = portname;
                break;
            } catch (Exception e) {
                tui.setMessage("Connect to '" + portname + "' failed - " + e.getMessage());
            }
        }
        if (!portok) {
            if (portnames.length == 1) {
                tui.setMessage("Connect to port " + portnames[0] + " failed. (see detailed messages in log)");
            } else {
                tui.setMessage("Connect to port failed. (see detailed messages in log)");
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
        int i;
        while (running) {
            try {
                Thread.sleep(10);
                var newcd = fillChannelData();
                if (newcd.valid) {
                    for (i = 1; i < 7; i++) {
                        if (newcd.ch[i] != oldcd.ch[i])
                            tui.notify(i, newcd.ch[i]);
                    }
                    oldcd = newcd;
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

    private void sendMessage(int type, byte ... data) throws IOException {
        out.write(0);
        out.write(MAGIC);
        out.write(type);
        out.write(data);
        out.write(0);
    }

    public byte[] getLatestSettingData() {
        return latestsd;
    }

    public boolean sendSettingsData(SettingData sd) {
        try {
            var baos = new ByteArrayOutputStream();
            sd.writeTo(baos);
            sendMessage(UPDTID, baos.toByteArray());
            return true;
        } catch (IOException e) {
            tui.addLogMsg("Send failed - " + e);
        }
        return false;
    }

    public void startCalibrate() {
        try {
            sendMessage(STARTC);
        } catch (Exception e) {
            tui.addLogMsg("Start calibrate failed - " + e);
        }
    }

    public void stopCalibrate() {
        try {
            sendMessage(STOPC);
        } catch (IOException e) {
            tui.addLogMsg("Stop calibrate failed - " + e);
        }
    }

    public void getSettingsData() {
        try {
            sendMessage(SENDID);
        } catch (IOException e) {
            tui.addLogMsg("Get failed - " + e);
        }
    }

    public ChannelData fillChannelData() {
        if (done == 0)
            tui.addLogMsg("starting fillChannelData");
        try {
            while (true) {
                int i = in.read();
                connected = true;
                if (i == MAGIC){
                    if (done == 0) {
                        done = 1;
                        tui.addLogMsg("MAGIC read");
                    }
                    i = in.read();
                    if (i == PUSHID) {
                        ChannelData cd = ChannelData.readFrom(in);
                        if (done < 2) {
                            done = 2;
                            tui.addLogMsg("PUSHID read");
                        }
                        return cd;
                    } else if (i == GETID) {
                        latestsd = in.readNBytes(SETTINGLEN);
                        SettingData sd = SettingData.readFrom(new ByteArrayInputStream(latestsd));
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
        } catch (IOException e) {
            connected = false;
            tui.setMessage("fillChannelData - data error - " + e);
            e.printStackTrace();
            return null;
        }
    }
}
