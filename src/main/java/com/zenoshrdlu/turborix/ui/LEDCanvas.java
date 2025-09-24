package com.zenoshrdlu.turborix.ui;

import java.awt.*;

public class LEDCanvas extends Canvas {
    Color color;

    public LEDCanvas(Color color) {
        super();
        this.color = color;
        setSize(20,20);
    }

    public Dimension getMinimumSize() {
        return new Dimension(20,20);
    }

    public void setColor(Color color) {
        this.color = color;
        repaint();
    }

    public void update(Graphics graphics) {
        paint(graphics);
    }

    public void paint(Graphics graphics) {
        Rectangle bounds = getBounds();
        graphics.setColor(color);
        graphics.fillOval(1,1, bounds.width - 2, bounds.height - 2);
        graphics.setColor(Color.black);
        graphics.drawOval(1,1, bounds.width - 2, bounds.height - 2);
    }
}
