package com.zenoshrdlu.turborix;

/*******************************************************************************
*                                                                              *
*                                                                              *
*******************************************************************************/

   import java.awt.*;

public class BorderPanel extends Panel {
   String title, midtitle;

/*----------------------------------------------------------------------------*/
/*  Construct a panel with a title                                            */
/*----------------------------------------------------------------------------*/
public BorderPanel(String t) {
      title = t;
      midtitle = null;
      setBackground(Color.lightGray);
   }

/*----------------------------------------------------------------------------*/
/*  Construct a panel with two titles (left and middle)                       */
/*----------------------------------------------------------------------------*/
   public BorderPanel(String t, String m) {
      title = t;
      midtitle = m;
      setBackground(Color.lightGray);
   }

/*----------------------------------------------------------------------------*/
/*  draw the control (border, titles                      )                   */
/*----------------------------------------------------------------------------*/
   public void paint(Graphics g) {
      Rectangle r = getBounds();
      g.setColor(Color.lightGray);
      g.fillRect(0,0,r.width, r.height);
      g.draw3DRect(4, 4, r.width - 8, r.height - 8, false);
      int w = g.getFontMetrics().stringWidth(title);
      g.fillRect(15,0,w,15);
      g.setColor(Color.black);
      g.drawString(title, 15, 10);
      if (midtitle != null) {
         w = g.getFontMetrics().stringWidth(midtitle);
         g.setColor(Color.lightGray);
         g.fillRect((r.width - w)/2,0,w,15);
         g.setColor(Color.black);
         g.drawString(midtitle, (r.width - w)/2, 10);         
      }
   }

   public Dimension getMinimumSize() {
      return new Dimension(100,100);
   }

//   public Dimension getPreferredSize() {
//      return getMinimumSize();
//   }

}
