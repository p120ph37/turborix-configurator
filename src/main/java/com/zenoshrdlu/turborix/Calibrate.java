package com.zenoshrdlu.turborix;

/******************************************************************************
*                                                                             *
* Turborix Configurator User Interface (Calibrate)                            *
* Dave Mitchell dave@zenoshrdlu.com                                           *
*                                                                             *
******************************************************************************/

import java.awt.*;

import java.awt.event.*;

import com.zenoshrdlu.turborix.io.TurborixEngine;
import com.zenoshrdlu.turborix.ui.SmartLayout;
import com.zenoshrdlu.turborix.ui.TextReadOnly;

public class Calibrate extends Dialog {

    static final String TITLE    = "Turborix Cailbration Screen";
    static final String START    = "Start Calibration";
    static final String NEXT     = "Next Step";
    static final String STOP     = "Stop Calibration";

    static final String[][] STEPS = {
        {
            "To start click 'Start Calibration'",
            ""
        },
        {
            "Step 1 of 3",
            "  move Left Up/Down trimmer UP as far as possible\r\n" +
            "  move Left Left/Right trimmer RIGHT as far as possible\r\n" +
            "  move Right Up/Down trimmer UP as far as possible\r\n" +
            "  move Right Left/Right trimmer RIGHT as far as possible\r\n" +
            "  move BOTH joysticks UP and RIGHT as far as possible\r\n" +
            "then click NEXT",
        },
        {
            "Step 2 of 3",
            "  move Left Up/Down trimmer DOWN as far as possible\r\n" +
            "  move Left Left/Right trimmer LEFT as far as possible\r\n" +
            "  move Right Up/Down trimmer DOWN as far as possible\r\n" +
            "  move Right Left/Right trimmer LEFT as far as possible\r\n" +
            "  move BOTH joysticks DOWN and LEFT as far as possible\r\n" +
            "then click NEXT",
        },
        {
            "Step 3 of 3",
            "  turn BOTH VR knobs fully CounterClockwise\r\n" +
            "  turn BOTH VR knobs fully Clockwise\r\n" +
            "then click NEXT",
        },
        {
            "Now click 'Stop Calibration'",
            ""
        },
        {
            "Calibration completed - you may close the window",
            ""
        }
    };

    TextField stepnum;
    TextArea  stepinfo;
    Button    start;
    Button    next;
    Button    stop;

    TurborixEngine tei;
    int step = 0;

    public Calibrate(TurborixConfig ui, TurborixEngine te) {
        super(ui, false);
        this.tei = te;
        setTitle(TITLE);
        setSize(376, 180);
        setLocation(150, 150);
        setBackground(Color.lightGray);
        setFont(new Font("SansSerif", Font.PLAIN, 12));
        setLayout(new SmartLayout("insets 2, gap 2 2"));

        add(stepnum = new TextReadOnly(0), "spanx 3");
        add(stepinfo = new TextArea("", 6, 30, TextArea.SCROLLBARS_NONE), "newline, pushy, spanx 3");

        /* buttons */
        add(start = new Button(START) {{
            addActionListener(event -> {
                setStep(1);
                tei.startCalibrate();
                System.out.println("Window size: " + Calibrate.this.getSize());
            });
        }}, "newline, pushx, growx 0, center");
        add(next = new Button(NEXT) {{
            addActionListener(event -> setStep(step + 1));
        }}, "pushx, growx 0, center");
        add(stop = new Button(STOP) {{
            addActionListener(event -> {
                setStep(5);
                tei.stopCalibrate();
            });
        }}, "pushx, growx 0, center");

        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent event) {
                setStep(5);
                dispose();
            }
        });

        setStep(0);
    }

    public void setStep(int step) {
        this.step = step;
        stepnum.setText(STEPS[step][0]);
        stepinfo.setText(STEPS[step][1]);
        if (step == 0) {
            start.setEnabled(true);
            next.setEnabled(false);
            stop.setEnabled(false);
        } else if (step < 4) {
            start.setEnabled(false);
            next.setEnabled(true);
            stop.setEnabled(true);
        } else if (step == 4) {
            start.setEnabled(false);
            next.setEnabled(false);
            stop.setEnabled(true);
        } else if (step == 5) {
            start.setEnabled(true);
            next.setEnabled(false);
            stop.setEnabled(false);
        }
    }
}