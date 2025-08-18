
/******************************************************************************
*                                                                             *
* GraphCanvas for Turborix Heli Configurator                                  *
*                                                                             *
*                                                                             *
******************************************************************************/

import java.awt.*;

public class GraphCanvas extends Canvas {
   static final int NPTS = 5;
   int[] xpoints;
   int[] ypoints;
   Color fill;
   Color grid;
   Color text;
   Color line;
   int height;
   int width;
   int[]ix, iy;
   
   public GraphCanvas() {
      super();
      int i;
      fill = Color.green;
      grid = Color.black;
      text = Color.black;
      line = Color.black;
      xpoints = new int[NPTS];
      ypoints = new int [NPTS];
      ix = new int[NPTS];
      iy = new int[NPTS];
      for (i=0; i<NPTS; i++) {
         xpoints[i] = 0;
         ix[i]=0;
         ypoints[i] = 0;
         iy[i]=0;
      }
   }

   public Dimension getMinimumSize() {
      return new Dimension(135,120);
   }
   
   public Dimension getPreferredSize() {
      return new Dimension(135,120);
   }
   
   public void setFill(Color c) {
      fill = c;
      repaint();
   }
   public void setPoints(int[] x, int[] y) {
      int i;
      for (i=0; i<NPTS;i++) {
         xpoints[i] =x[i];
         ypoints[i] = y[i];
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
//      g.drawLine(iw/2, -ih/2, iw/2, -height);         
//      g.drawLine(iw/2, -ih/2, width, - ih/2);      
      g.setColor(text);
      for (i=0; i < NPTS; i++) {
         g.drawString("P" + (i+1),  iw + iw*i, -5);
      }
      for (i=0; i < NPTS; i++) {
         g.drawString("" + 25*i, 5, -ih*i - ih);
      }
      g.setColor(line);
      for (i=0; i < NPTS;i++) {
         ix[i] = iw + xpoints[i]*NPTS*iw/100; 
         iy[i] = -ih - ypoints[i]*NPTS*ih/100; 
      }
      g.drawPolyline(ix, iy, NPTS);
   }
}
