package com.zenoshrdlu.turborix;

/******************************************************************************
*                                                                             *
*                                                                             *
*                                                                             *
******************************************************************************/


   class ChannelData {
      
      public int[] ch;
      int chksum = 0;
      boolean valid = false;

      public ChannelData() {
         int i;
         ch = new int[8];
         for (i=0; i < 8; i++)
            ch[i] = -1;
      }
      
      public ChannelData(int[] d) {
         int i, n;
         ch = new int[8];
         chksum = 0;
          for (i=0; i<7;i++) {  
            ch[i+1] = 256*d[2*i] + d[2*i+1] - 1000;
            chksum += d[2*i] + d[2*i+1];
         }
         valid = true;
         n = 256*d[14] + d[15];
         if (n != chksum) {
            // System.out.println("Error - chksum = " + chksum + " (" + n + ")");
            valid = false;
         }
      }
   }
