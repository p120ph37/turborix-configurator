package com.zenoshrdlu.turborix.ui;

/******************************************************************************
*                                                                             *
* GraphCanvas for Turborix Heli Configurator                                  *
*                                                                             *
******************************************************************************/

import java.awt.*;
import java.util.stream.IntStream;

public class GraphCanvas extends Canvas {
    final int npts;
    final int[] xpoints, ypoints;
    Color fill, grid, text, line;
    int width, height;

    public GraphCanvas() {
        this(5);
    }

    public GraphCanvas(int npts) {
        super();
        this.npts = npts;
        this.fill = Color.green;
        this.grid = Color.black;
        this.text = Color.black;
        this.line = Color.black;
        this.xpoints = new int[npts];
        this.ypoints = new int[npts];
    }

    public Dimension getMinimumSize() {
        return new Dimension(135, 120);
    }

    public Dimension getPreferredSize() {
        return getMinimumSize();
    }

    public void setFill(Color c) {
        this.fill = c;
        repaint();
    }

    public void setPoints(int[] x, int[] y) {
        for (int i = 0; i < this.npts; i++) {
            this.xpoints[i] = x[i];
            this.ypoints[i] = y[i];
        }
        repaint();
    }

    public void update(Graphics g) {
        paint(g);
    }

    public void paint(Graphics g) {
        int i, ih, iw;
        g.setPaintMode();
        Dimension d = getSize();
        width  = d.width;
        height = d.height;
        g.translate(0, height);
        iw = width/6;
        ih = height/6;
        g.setColor(fill);
        g.fillRect(0, -height, width, height);
        g.setColor(grid);
        g.setColor(text);
        for (i = 0; i < this.npts; i++) {
            g.drawString("P" + (i + 1),  iw + iw * i, -5);
        }
        for (i = 0; i < this.npts; i++) {
            g.drawString("" + 25 * i, 5, -ih * i - ih);
        }
        g.setColor(line);
        g.drawPolyline(
            IntStream.of(xpoints).map(x -> iw + x * this.npts * iw / 100).toArray(),
            IntStream.of(ypoints).map(y -> -ih - y * this.npts * ih / 100).toArray(),
            this.npts
        );
    }
}
