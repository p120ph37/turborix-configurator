package com.zenoshrdlu.turborix;

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
// v 2.0.0 - Forked and updated for JDK 17 and jSerialComm

import java.awt.*;

import java.awt.event.*;
import java.util.*;
import java.util.List;

import com.zenoshrdlu.turborix.io.TurborixEngine;
import com.zenoshrdlu.turborix.settings.SettingData;
import com.zenoshrdlu.turborix.ui.*;

import static java.util.stream.IntStream.range;

import java.io.*;

public class TurborixConfig extends Frame {

    static final Properties VERSIONING = new Properties();
    static {
        try (
            InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream("versioning.properties");
        ) {
            VERSIONING.load(is);
        } catch(IOException e) {
            throw new RuntimeException("Unable to load versioning information");
        }
    }

    static final String TITLE    = VERSIONING.getProperty("title") + " - " + VERSIONING.getProperty("vendor");
    static final String VERSION  = VERSIONING.getProperty("version");
    static final String GET      = "Get Settings";
    static final String SEND     = "Send Settings";
    static final String SAVE     = "Save Settings";
    static final String RESTORE  = "Restore Settings";
    static final String CALIBRATE= "Calibrate";
    static final String EXIT     = "Exit";

    static final String STICK    = "Stick";
    static final String MODE     = "Mode";
    static final String ACRO     = "ACRO";
    static final String HELI120  = "HELI-120";
    static final String HELI90   = "HELI-90";
    static final String HELI140  = "HELI-140";

    static final String CHANNEL  = "Channel";
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
    static final String DEFH     = "688";
    static final String DEFW     = "752";
    static final String PPROP    = "port";
    static final String[] PORTNAMES = new String[] {
        "/dev/tty-usbserial",
        "/dev/tty.usbserial",
        "/dev/tty.SLAB_USBtoUART"
    };
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

    Choice stick, mode;
    Label ln;
    TextField fn;
    TextField port;

    TextField ch[] = new TextField[6];
    Scrollbar hchscroll[] = new Scrollbar[6];

    LEDCanvas connected;
    LEDCanvas synced;

    Choice nr[] = new Choice[6];
    TextField epa[] = new TextField[6];
    TextField epb[] = new TextField[6];
    TextField subt[] = new TextField[6];

    TextField dualon1, dualoff1, dualon2, dualoff2, dualon4, dualoff4;

    Choice switcha, switchb;

    Choice variablea, variableb;

    Choice mixsrc[] = new Choice[3];
    Choice mixdest[] = new Choice[3];
    TextField mixup[] = new TextField[3];
    TextField mixdown[] = new TextField[3];
    Choice mixsw[] = new Choice[3];

    TextField message;
    Button    get, send, save, restore, calibrate, exit;

    Properties props   = new Properties();
    boolean loaded = false;
    int		  hprop;
    int		  wprop;

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
        super(TITLE + " - " + VERSION);
        setLayout(new SmartLayout("insets 2, gap 2 2"));
        setBackground(Color.lightGray);
        setFont(new Font("SansSerif", Font.PLAIN, 12));
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent event) {
                closeLog();
                doClose();
            }
        });

        filedialog = new FileDialog(this);
        folder = getProperty(FLDPROP, System.getProperty(USERDIR));
        logging = getProperty(LOGGING, "YES").toUpperCase().equals(YES);
        openLog(getProperty(LOGPROP, DEFLOG));

        /* Channel Data */
        add(new BorderPanel("Channel Data") {{
            for (int i = 0; i < 6; i++) {
                add(new Label(CHANNEL + (1 + i)), "newline");
                add(ch[i] = new TextReadOnly(4));
                add(hchscroll[i] = new Scrollbar(Scrollbar.HORIZONTAL, 0, 1, -100, 100), "push, grow");
            }
        }}, "spanx, push");

        /* normal/reverse and end points */
        add(new BorderPanel("Output Ranges") {{
            add(new Label("Normal/Reverse"), "growx 0, center, spanx 2");
            add(new Label("End Points"), "growx 0, center, spanx 2");
            add(new Label("SubTrim"), "growx 0, center");
            for (int i = 0; i < 6; i++) {
                add(new Label(CH + (1 + i)), "newline");
                add(nr[i] = new Choice() {{
                    add(NORMAL);
                    add(REVERSE);
                }});
                add(epa[i] = new Text());
                add(epb[i] = new Text());
                add(subt[i] = new Text());
            }
        }}, "newline, pushx");

        /* dual rates */
        add(new BorderPanel("Dual Rate") {{
            add(new Label("On"), "growx 0, center, skip 1");
            add(new Label("Off"), "growx 0, center");
            add(new Label(CH + 1), "newline");
            add(dualon1 = new Text());
            add(dualoff1 = new Text());
            add(new Label(CH + 2), "newline");
            add(dualon2 = new Text());
            add(dualoff2 = new Text());
            add(new Label(CH + 4), "newline");
            add(dualon4 = new Text());
            add(dualoff4 = new Text());
        }}, "split, flowy, pushx");

        /* switches and variables */
        add(new BorderPanel("Switches & Variables") {{
            add(new Label(SWITCH + "A"), "newline");
            add(switcha = new Choice() {{
                add(NULL);
                add(DUAL);
                add(THROT);
                add(NORMID);
            }});
            add(new Label(SWITCH + "B"), "newline");
            add(switchb = new Choice() {{
                add(NULL);
                add(DUAL);
                add(THROT);
                add(NORMID);
            }});
            add(new Label(VARIABLE + "A"), "newline");
            add(variablea = new Choice() {{
                add(NULL);
                add(ADJUST);
            }});
            add(new Label(VARIABLE + "B"), "newline");
            add(variableb = new Choice() {{
                add(NULL);
                add(ADJUST);
            }});
        }}, "skip");

        /* mode and stick */
        add(new BorderPanel("Mode & Stick") {{
            add(new Label(MODE), "newline");
            add(mode = new Choice() {{
                add(ACRO);
                add(HELI120);
                add(HELI90);
                add(HELI140);
                addItemListener(event -> hc.setVisible(mode.getSelectedIndex() != 0));
            }});
            add(new Label(STICK), "newline");
            add(stick = new Choice() {{
                range(0, 4).forEach(i -> add(MODE + (1 + i)));
            }});
        }}, "split, flowy, pushx");

        /* status */
        add(new BorderPanel("Status") {{
            add(new Panel(new SmartLayout("insets 0")) {{
                add(new Label(CONNECTED), "newline");
                add(connected = new LEDCanvas(Color.red), "grow 0 0");
                add(new Label("Port"), "newline");
                add(port = new TextReadOnly(10));
                add(new Label(SYNCED), "newline");
                add(synced = new LEDCanvas(Color.red), "grow 0 0");
            }});
            add(ln = new Label(LOADFRM), "newline, growx 0, center");
            add(fn = new TextReadOnly(20), "newline");
        }});

        /* Mixes */
        add(new BorderPanel("Mixes") {{
            add(new Label("Mix 1"), "growx 0, center, skip 1");
            add(new Label("Mix 2"), "growx 0, center");
            add(new Label("Mix 3"), "growx 0, center");
            add(new Label(SOURCE), "newline");
            range(0, 3).forEach(i -> add(mixsrc[i] = new MixSrc(), "growx, gap 0 0 0 0"));
            add(new Label(DEST), "newline");
            range(0, 3).forEach(i -> add(mixdest[i] = new MixDest(), "growx, gap 0 0 0 0"));
            add(new Label(UP), "newline");
            range(0, 3).forEach(i -> add(mixup[i] = new Text(4), "growx"));
            add(new Label(DOWN), "newline");
            range(0, 3).forEach(i -> add(mixdown[i] = new Text(4), "growx"));
            add(new Label(SWITCH), "newline");
            range(0, 3).forEach(i -> add(mixsw[i] = new MixSw(), "growx, gap 0 0 0 0"));
        }}, "newline, spanx");

        /* message area */
        add(message = new TextReadOnly(60), "newline, spanx, growx");

        /* buttons */
        add(get = new Button(GET) {{
            setEnabled(false);
            addActionListener(event -> {
                te.getSettingsData();
                ln.setText(LOADFRM);
                fn.setText(TRANSMIT);
            });
        }}, "newline, spanx, split, center");
        add(send = new Button(SEND) {{
            setEnabled(false);
            addActionListener(event -> {
                setMessage("");
                var sd = buildSettings();
                if (sd != null) {
                    if (te.sendSettingsData(sd))
                        setMessage("Settings transmitted successfully");
                    else
                        setMessage("Send failure");
                } else {
                    setMessage("data invalid - send cancelled");
                }
            });
        }});
        add(save = new Button(SAVE) {{
            setEnabled(false);
            addActionListener(event -> {
                setMessage("");
            try {
                var sd = buildSettings();
                if (sd == null) {
                    setMessage("data is invalid");
                    return;
                }
                filedialog.setTitle(SAVEDLG);
                filedialog.setMode(FileDialog.SAVE);
                filedialog.setFile("*.trb");
                filedialog.setDirectory(folder);
                filedialog.setVisible(true);
                var filename = filedialog.getFile();
                if (filename != null) {
                    try (
                        var out = new FileOutputStream(filename)
                    ) {
                        sd.writeTo(out);
                    }
                    setMessage("Settings saved");
                    ln.setText(SAVEDAS);
                    fn.setText(filename);
                }
            } catch (Exception e) {
                setMessage("Error obtaining savefile name");
            }
            });
        }});
        add(restore = new Button(RESTORE) {{
            addActionListener(event -> {
                setMessage("");
                try {
                    filedialog.setTitle(RESTDLG);
                    filedialog.setMode(FileDialog.LOAD);
                    filedialog.setFile("*.trb");
                    filedialog.setDirectory(folder);
                    filedialog.setVisible(true);
                    var filename = filedialog.getFile();
                    if (filename != null) {
                        folder = filedialog.getDirectory();
                        try (
                            var in = new FileInputStream(filename)
                        ) {
                            var sdata = SettingData.readFrom(in);
                            if (sdata.valid) {
                                applySettings(sdata);
                                hc.applySettings(sdata);
                                ln.setText(LOADFRM);
                                fn.setText(filename);
                            } else {
                                setMessage("Bad checksum");
                            }
                        }
                    }
                } catch (Exception e) {
                    setMessage("Error obtaining restore file name " + e);
                }
            });
        }});
        add(calibrate = new Button(CALIBRATE) {{
            setEnabled(false);
            addActionListener(event -> {
                setMessage("Calibrating");
                cdlg.setVisible(true);
            });
        }});
        add(exit = new Button(EXIT) {{
            addActionListener(event -> {
                te.stop();
                doClose();
            });
        }});

        hprop = getInt(getProperty(HPROP, DEFH));
        wprop = getInt(getProperty(WPROP, DEFW));

        String portname = getProperty(PPROP, null);

        setSize(wprop, hprop);
        setLocation(50,50);
        setVisible(true);

        te = new TurborixEngine(this, Optional.ofNullable(portname).map(s -> new String[] {s}).orElse(PORTNAMES));
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
            port.setText(te.getConnectPort());
        }
        d = (d * 200) / 1024 - 100;
        if (c > 0 && c <= 6) {
            ch[c - 1].setText("" + d);
            hchscroll[c - 1].setValue(d);
        }
    }

    public int validateChoice(int num, int v, int n) {
        if (v < 0 || v >= n) {
            addLogMsg("Choice " + num + " value " + v + " out of range " + 0 + ":" + n);
            return 0;
        } else {
            return v;
        }
    }

    public void validateChoices(SettingData sdata) {
              mode.select(validateChoice(1, sdata.model, 4));
             stick.select(validateChoice(2, sdata.stick, 4));
             nr[0].select(sdata.reverse.get(0) ? 1 : 0);
             nr[1].select(sdata.reverse.get(1) ? 1 : 0);
             nr[2].select(sdata.reverse.get(2) ? 1 : 0);
             nr[3].select(sdata.reverse.get(3) ? 1 : 0);
             nr[4].select(sdata.reverse.get(4) ? 1 : 0);
             nr[5].select(sdata.reverse.get(5) ? 1 : 0);
          switcha.select(validateChoice(9, sdata.switchA, 4));
        variablea.select(validateChoice(10, sdata.variableA, 2));
          switchb.select(validateChoice(11, sdata.switchB, 4));
        variableb.select(validateChoice(12, sdata.variableB, 2));
        mixsrc[0].select(validateChoice(13, sdata.mixer[0].src, 8));
        mixsrc[1].select(validateChoice(14, sdata.mixer[1].src, 8));
        mixsrc[2].select(validateChoice(15, sdata.mixer[2].src, 8));
      mixdest[0].select(validateChoice(16, sdata.mixer[0].dst, 6));
      mixdest[1].select(validateChoice(17, sdata.mixer[1].dst, 6));
      mixdest[2].select(validateChoice(18, sdata.mixer[2].dst, 6));
         mixsw[0].select(validateChoice(19, sdata.mixer[0].sw, 4));
         mixsw[1].select(validateChoice(20, sdata.mixer[1].sw, 4));
         mixsw[2].select(validateChoice(21, sdata.mixer[2].sw, 4));
    }

    public void applySettings(SettingData sdata) {
        save.setEnabled(true);
        hc.setVisible(sdata.model != 0);
        validateChoices(sdata);
        for (int i = 0; i < 6; i++) {
            epa[i].setText("" + sdata.end[i].down);
            epb[i].setText("" + sdata.end[i].up);
            subt[i].setText("" + sdata.subtrim[i]);
        }
        dualon1.setText("" + sdata.dual[0].on);
        dualon2.setText("" + sdata.dual[1].on);
        dualon4.setText("" + sdata.dual[2].on);
        dualoff1.setText("" + sdata.dual[0].off);
        dualoff2.setText("" + sdata.dual[1].off);
        dualoff4.setText("" + sdata.dual[2].off);
        for (int i = 0; i < 3; i++) {
            mixup[i].setText("" + sdata.mixer[i].up);
            mixdown[i].setText("" + sdata.mixer[i].dn);
        }
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
        t.setBackground(e ? Color.red : Color.cyan);
        return e ? 1 : 0;
    }

    public int validateField(TextField t, int lo, int hi) {
        var i = getInt(t.getText());
        var e = flagError(t, (i < lo || i > hi));
        if (e == 1)
            addLogMsg("Field " + t + " value " + i + " out of range " + lo + ":" + hi);
        return e;
    }

    public int validateFields() {
        int j = 0;
        for (int i = 0; i < 6; i++) {
            j += validateField(epa[i], 0, 120);
            j += validateField(epb[i], 0, 120);
            j += validateField(subt[i], -120, 120);
        }
        j += validateField(dualon1, 0, 100);
        j += validateField(dualon2, 0, 100);
        j += validateField(dualon4, 0, 100);
        j += validateField(dualoff1, 0, 100);
        j += validateField(dualoff2, 0, 100);
        j += validateField(dualoff4, 0, 100);
        for (int i = 0; i < 3; i++) {
            j += validateField(mixup[i], -100, 100);
            j += validateField(mixdown[i], -100, 100);
        }
        return j;
    }

    public SettingData buildSettings() {
        var sd = new SettingData();
        if (validateFields() != 0) {
            setMessage("" + validateFields() + " value(s) invalid");
            return null;
        }

        sd.model = mode.getSelectedIndex();
        sd.stick = stick.getSelectedIndex();
        for (int i = 0; i < 6; i++) {
            sd.reverse.set(i, nr[i].getSelectedIndex());
        }

        // sd[2-7] = dualonoffs
        sd.dual[0].on = getInt(dualon1.getText());
        sd.dual[0].off = getInt(dualoff1.getText());
        sd.dual[1].on = getInt(dualon2.getText());
        sd.dual[1].off = getInt(dualoff2.getText());
        sd.dual[2].on = getInt(dualon4.getText());
        sd.dual[2].off = getInt(dualoff4.getText());

        // sd[8-10] heli stuff - leave as-is

        // sd[11-22] = ends */
        for (int i = 0; i < 6; i++) {
            sd.end[i].down = getInt(epa[i].getText());
            sd.end[i].up = getInt(epb[i].getText());
        }

        // sd[23-42] more heli stuff - leave as-is

        // sd[43-48] subtrims (2's complement)
        for (int i = 0; i < 6; i++) {
            sd.subtrim[i] = getInt(subt[i].getText());
        }

        // sd[49-60] mixers
        for (int i = 0; i < 3; i++) {
            sd.mixer[i].src = mixsrc[i].getSelectedIndex();
            sd.mixer[i].dst = mixdest[i].getSelectedIndex();
            sd.mixer[i].up = getInt(mixup[i].getText());
            sd.mixer[i].dn = getInt(mixdown[i].getText());
            sd.mixer[i].sw = mixsw[i].getSelectedIndex();
        }

        sd.switchA = switcha.getSelectedIndex();
        sd.switchB = switchb.getSelectedIndex();
        sd.variableA = variablea.getSelectedIndex();
        sd.variableB = variableb.getSelectedIndex();

        // if heli config then build heli settings
        hc.buildSettings(sd);

        return sd;
    }

    public int getInt(String s) {
        try {
            return Integer.parseInt(s);
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
    public void addLogMsg(String s) {
        if (logging)
            try {
                logOut.writeBytes(s + "\r\n");
                logOut.flush();
            } catch(IOException e) {
                setMessage("Error in writing to log file " + e);
            }
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
    public String getProperty(String prop, String def) {
        if (!loaded)
            loadProperties(PROPS);
        return (String)props.getProperty(prop, def);
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

    static class MixDest extends Choice {
        public MixDest() {
            List.of("1", "2", "3", "4", "5", "6").forEach(n -> add(CHANNEL + n));
        }
    }

    static class MixSrc extends MixDest {
        public MixSrc() {
            super();
            add(VARIABLE + "A");
            add(VARIABLE + "B");
        }
    }

    static class MixSw extends Choice {
        public MixSw() {
            add(SWITCH + "A");
            add(SWITCH + "B");
            add(ON);
            add(OFF);
        }
    }
}
