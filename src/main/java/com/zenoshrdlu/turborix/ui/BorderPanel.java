package com.zenoshrdlu.turborix.ui;

import java.awt.*;

public class BorderPanel extends Panel {
    final private String title;

    /*----------------------------------------------------------------------------*/
    /*  Construct a panel with a title and optional SmartLayout constraints       */
    /*----------------------------------------------------------------------------*/
    public BorderPanel(String title, String ...layoutConstraints) {
        super(new SmartLayout(layoutConstraints));
        this.title = title;
        setBackground(Color.lightGray);
    }

    /*----------------------------------------------------------------------------*/
    /*  draw the control (border, title)                                          */
    /*----------------------------------------------------------------------------*/
    public void paint(Graphics g) {
        Rectangle r = getBounds();
        String t = " " + title + " ";
        g.setColor(Color.lightGray);
        g.fillRect(0,0,r.width, r.height);
        g.draw3DRect(4, 4, r.width - 8, r.height - 8, false);
        int w = g.getFontMetrics().stringWidth(t);
        g.fillRect(15, 0, w, 15);
        g.setColor(Color.black);
        g.drawString(t, 15, 10);
    }

    public Dimension getMinimumSize() {
        return new Dimension(16,16);
    }
}
