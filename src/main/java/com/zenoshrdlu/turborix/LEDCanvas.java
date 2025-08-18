package com.zenoshrdlu.turborix;

/******************************************************************************
*                                                                             *
*                                                                             *
******************************************************************************/

import java.awt.*;

public class LEDCanvas extends Canvas {
   Color color = Color.red;;
   
   public LEDCanvas(Color c) {
       super();
       color = c;
       setSize(20,20);
   }

   public Dimension getMinimumSize() {
      return new Dimension(20,20);
   }

   public void setColor(Color c) {
      color = c;
      repaint();
   }     

   public void update(Graphics g) {
      paint(g);
   }

   public void paint(Graphics g) {
       Rectangle r = getBounds();
       g.setColor(color);
       g.fillOval(1,1, r.width - 2, r.height - 2);
       g.setColor(Color.black);
       g.drawOval(1,1, r.width - 2, r.height - 2);
   }
}
