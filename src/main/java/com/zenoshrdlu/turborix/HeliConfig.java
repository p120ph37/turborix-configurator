package com.zenoshrdlu.turborix;

/******************************************************************************
*                                                                             *
* Turborix Configurator User Interface (Heli fields)                          *
* Dave Mitchell dave@zenoshrdlu.com                                           *
*                                                                             *
******************************************************************************/

import java.awt.*;
import java.awt.event.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import com.zenoshrdlu.turborix.io.TurborixEngine;
import com.zenoshrdlu.turborix.settings.SettingData;
import com.zenoshrdlu.turborix.ui.BorderPanel;
import com.zenoshrdlu.turborix.ui.GraphCanvas;
import com.zenoshrdlu.turborix.ui.SmartLayout;
import com.zenoshrdlu.turborix.ui.Text;

public class HeliConfig extends Dialog {

    static final String TITLE    = "Turborix Heli Configuration Screen";
    static final String THROTTLE = "Throttle";
    static final String PITCH    = "Pitch";
    static final String SWASH    = "SWASH AFR";
    static final String CHANNEL  = "Chan ";
    static final String SNORMAL  = "Show Normal";
    static final String SIDLE    = "Show Idle";
    static final String NORMAL   = "Normal";
    static final String IDLE     = "Idle";

    TextField[][] throttle = new TextField[5][2];
    TextField[][] pitch = new TextField[5][2];
    TextField[] swash = new TextField[3];

    Button    tbut, pbut;
    GraphCanvas tgraph, pgraph;

    boolean showtn = true;
    boolean showpn = true;

    final int[] x = new int[] {0, 25, 50, 75, 100};

    final TurborixConfig tui;

    public HeliConfig(TurborixConfig ui, TurborixEngine te) {
        super(ui, false);
        this.tui = ui;
        setTitle(TITLE);
        setSize(552, 280);
        setLocation(150,150);
        setBackground(Color.lightGray);
        setFont(new Font("SansSerif", Font.PLAIN, 12));
        setLayout(new SmartLayout("insets 2, gap 2 2"));

        /* throttle panel */
        add(new BorderPanel(THROTTLE, "insets 16 8 8 8") {{
            add(new Panel(new SmartLayout("insets 0")) {{
                add(new Label(NORMAL), "skip 1");
                add(new Label(IDLE));
                for (int i = 0; i < 5; i++) {
                    add(new Label("P" + (i + 1)), "newline");
                    add(throttle[i][0] = new Text());
                    add(throttle[i][1] = new Text());
                }
            }}, "growy 0, spany 2");
            add(new Label(" "), "");
            add(tgraph = new GraphCanvas(), "newline, push");
            add(tbut = new Button(SIDLE) {{
                addActionListener(event -> {
                    showtn = !showtn;
                    showGraphs();
                });
            }}, "newline, skip 1, growx 0, center");
        }}, "push");

        /* pitch panel */
        add(new BorderPanel(PITCH, "insets 16 8 8 8") {{
            add(new Panel(new SmartLayout("insets 0")) {{
                add(new Label(NORMAL), "skip 1");
                add(new Label(IDLE));
                for (int i = 0; i < 5; i++) {
                    add(new Label("P" + (i + 1)), "newline");
                    add(pitch[i][0] = new Text());
                    add(pitch[i][1] = new Text());
                }
            }}, "growy 0, spany 2");
            add(new Label(" "));
            add(pgraph = new GraphCanvas(), "newline, push");
            add(pbut = new Button(SIDLE) {{
                addActionListener(event -> {
                    showpn = !showpn;
                    showGraphs();
                });
            }}, "newline, skip 1, growx 0, center");
        }}, "push");

        /* swash panel */
        add(new BorderPanel(SWASH) {{
            add(new Label(CHANNEL + 1));
            add(new Label(CHANNEL + 2));
            add(new Label(CHANNEL + 6));
            add(swash[0] = new Text(), "newline");
            add(swash[1] = new Text());
            add(swash[2] = new Text());
        }}, "newline, spanx");

        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent event) {
                dispose();
            }
        });
    }

    public void applySettings(SettingData sd) {
        for (int i = 0; i < 5; i++) {
            throttle[i][0].setText("" + sd.throttlecurve[i].normal);
            throttle[i][1].setText("" + sd.throttlecurve[i].idle);
            pitch[i][0].setText("" + sd.pitchcurve[i].normal);
            pitch[i][1].setText("" + sd.pitchcurve[i].idle);
        }
        for (int i = 0; i < 3; i++) {
            swash[i].setText("" + sd.swash[i]);
        }
        validateFields();
        showGraphs();
    }

    public void buildSettings(SettingData sd) {
        if (validateFields() != 0) {
            tui.setMessage("" + validateFields() + " heli value(s) invalid");
            return;
        }
        for (int i = 0; i < 3; i++) {
            sd.swash[i] = getInt(swash[i]);
        }
        for (int i = 0; i < 5; i++) {
            sd.throttlecurve[i].normal = getInt(throttle[i][0]);
            sd.throttlecurve[i].idle = getInt(throttle[i][1]);
            sd.pitchcurve[i].normal = getInt(pitch[i][0]);
            sd.pitchcurve[i].idle = getInt(pitch[i][1]);
        }
    }

    public void notifySettings(SettingData sdata) {
        applySettings(sdata);
    }

    public boolean flagError(TextField t, boolean e) {
        t.setBackground(e ? Color.red : Color.cyan);
        return e;
    }

    public boolean validateField(TextField t, int lo, int hi) {
        int i = getInt(t);
        boolean e = flagError(t, (i < lo || i > hi));
        if (e)
            tui.addLogMsg("Field " + t + " value " + i + " out of range " + lo + ":" + hi);
        return e;
    }

    public int validateFields() {
        return Stream.concat(
            IntStream.range(0, 5).mapToObj(Integer::valueOf).flatMap(i ->
                Stream.of(throttle[i][0], throttle[i][1], pitch[i][0], pitch[i][1])
            ),
            Stream.of(swash[0], swash[1], swash[2])
        ).filter(t -> validateField(t, -100, 100)).collect(Collectors.counting()).intValue();
    }

    public int[] getTYPoints(boolean norm) {
        return IntStream.range(0, 5).map(i -> getInt(throttle[i][norm ? 0 : 1])).toArray();
    }

    public int[] getPYPoints(boolean norm) {
        return IntStream.range(0, 5).map(i -> getInt(pitch[i][norm ? 0 : 1])).toArray();
    }

    private static int getInt(TextField t) {
        try {
            return Integer.parseInt(t.getText());
        } catch (Exception e) {
            return -999;
        }
    }

    public void showGraphs() {
        tbut.setLabel(showtn ? SIDLE : SNORMAL);
        tgraph.setFill(showtn ? Color.green : Color.red);
        tgraph.setPoints(x, getTYPoints(showtn));
        pbut.setLabel(showpn ? SIDLE : SNORMAL);
        pgraph.setFill(showpn ? Color.green : Color.red);
        pgraph.setPoints(x, getPYPoints(showpn));
    }
}
