
/******************************************************************************
*                                                                             *
*                                                                             *
*                                                                             *
******************************************************************************/
                                  

   class SettingData {
      
      public static final int ACRO    = 0;
      public static final int HELI120 = 1;
      public static final int HELI90  = 2;
      public static final int HELI140 = 3;
      
      public static final int NORMAL = 0;
      public static final int REVERSE = 1;

      public static final int DUAL1 = 0;
      public static final int DUAL2 = 1;
      public static final int DUAL4 = 2;
      
      public static final int SWASH1 = 0;
      public static final int SWASH2 = 1;
      public static final int SWASH6 = 2;
      
      public static final int CNORMAL = 0;
      public static final int CID = 1;
      
      public static final int MIXER1 = 0;
      public static final int MIXER2 = 1;
      public static final int MIXER3 = 2;
      
      public static final int SRC = 0;
      public static final int DEST = 1;
      public static final int UPRATE = 2;
      public static final int DOWNRATE = 3;
      public static final int SWITCH = 4;
      
      public static final int SWA = 0;
      public static final int SWB = 1;
      public static final int ON = 2;
      public static final int OFF = 3;
      
      public static final int NULL   = 0;
      public static final int DUAL   = 1;
      public static final int THROT  = 2;
      public static final int NORMID = 3; 


      public boolean valid = false;
      public int model;
      public int stick;
      public int[] reverse = new int[6];
      public int[] dualon = new int[3];
      public int[] dualoff = new int[3];
      public int[] swash = new int[3];
      public int[][] end = new int[6][2];
      public int[][] throttlecurve = new int[5][2];
      public int[][] pitchcurve = new int[5][2];
      public int[] subtrim = new int[6];
      public int[][] mixer = new int[3][5];
      public int switchA;
      public int switchB;  
      public int variableA;
      public int variableB;    
      
      int chksum = 0;

      public SettingData() {
      }
      
      public SettingData(int[] d) {
         int i, j, n;
               
         chksum = 0;
         for (i=0; i<65;i++) {  
            chksum += d[i];
         }
         n = 256*d[65] + d[66];
         if (n != chksum) {
//            System.out.println("Error - setting chksum = " + chksum + " (" + n + ")");
            valid = false;
            return;
         }
         model = (d[0] & 0x0f); // was >>4
         stick = (d[0] & 0xf0) >> 4; // was null
         j = 1;
         for (i = 0; i < 6; i++) {
            n = d[1] & j;
            reverse[i] = (n != 0)? REVERSE : NORMAL;
            j = j << 1;
         }
         dualon[0]  = d[2];
         dualoff[0] = d[3];
         dualon[1]  = d[4];
         dualoff[1] = d[5];
         dualon[2]  = d[6];
         dualoff[2] = d[7];
         
         swash[0] = d[8] > 127 ? d[8] - 256 : d[8];
         swash[1] = d[9] > 127 ? d[9] - 256 : d[9];
         swash[2] = d[10] > 127 ? d[10] - 256 : d[10];
         
         end[0][0]  = d[11];
         end[0][1]  = d[12];
         end[1][0]  = d[13];
         end[1][1]  = d[14];
         end[2][0]  = d[15];
         end[2][1]  = d[16];
         end[3][0]  = d[17];
         end[3][1]  = d[18];
         end[4][0]  = d[19];
         end[4][1]  = d[20];
         end[5][0]  = d[21];
         end[5][1]  = d[22];
         
         throttlecurve[0][0] = d[23] > 127 ? d[23] - 256 : d[23];;
         throttlecurve[0][1] = d[24] > 127 ? d[24] - 256 : d[24];;
         throttlecurve[1][0] = d[25] > 127 ? d[25] - 256 : d[25];;
         throttlecurve[1][1] = d[26] > 127 ? d[26] - 256 : d[26];;
         throttlecurve[2][0] = d[27] > 127 ? d[27] - 256 : d[27];;
         throttlecurve[2][1] = d[28] > 127 ? d[28] - 256 : d[28];;
         throttlecurve[3][0] = d[29] > 127 ? d[29] - 256 : d[29];;
         throttlecurve[3][1] = d[30] > 127 ? d[30] - 256 : d[30];;
         throttlecurve[4][0] = d[31] > 127 ? d[31] - 256 : d[31];;
         throttlecurve[4][1] = d[32] > 127 ? d[32] - 256 : d[32];;
         
         pitchcurve[0][0] = d[33] > 127 ? d[33] - 256 : d[33];;
         pitchcurve[0][1] = d[34] > 127 ? d[34] - 256 : d[34];;
         pitchcurve[1][0] = d[35] > 127 ? d[35] - 256 : d[35];;
         pitchcurve[1][1] = d[36] > 127 ? d[36] - 256 : d[36];;
         pitchcurve[2][0] = d[37] > 127 ? d[37] - 256 : d[37];;
         pitchcurve[2][1] = d[38] > 127 ? d[38] - 256 : d[38];;
         pitchcurve[3][0] = d[39] > 127 ? d[39] - 256 : d[39];;
         pitchcurve[3][1] = d[40] > 127 ? d[40] - 256 : d[40];;
         pitchcurve[4][0] = d[41] > 127 ? d[41] - 256 : d[41];;
         pitchcurve[4][1] = d[42] > 127 ? d[42] - 256 : d[42];;
  
         subtrim[0] = d[43] > 127 ? d[43] - 256 : d[43];         
         subtrim[1] = d[44] > 127 ? d[44] - 256 : d[44];         
         subtrim[2] = d[45] > 127 ? d[45] - 256 : d[45];         
         subtrim[3] = d[46] > 127 ? d[46] - 256 : d[46];         
         subtrim[4] = d[47] > 127 ? d[47] - 256 : d[47];         
         subtrim[5] = d[48] > 127 ? d[48] - 256 : d[48]; 
        
         i = (d[49] & 0xf0) >> 4; // high nibble is source
         j = (d[49] & 0x0f); // low nibble is dest
         mixer[0][0] = i;
         mixer[0][1] = j;
         mixer[0][2] = d[50] > 127 ? d[50] - 256 : d[50];
         mixer[0][3] = d[51] > 127 ? d[51] - 256 : d[51];
         mixer[0][4] = d[52];
         
         i = (d[53] & 0xf0) >> 4;
         j = (d[53] & 0x0f);
         mixer[1][0] = i;
         mixer[1][1] = j;
         mixer[1][2] = d[54] > 127 ? d[54] - 256 : d[54];
         mixer[1][3] = d[55] > 127 ? d[55] - 256 : d[55];
         mixer[1][4] = d[56];
         
         i = (d[57] & 0xf0) >> 4;
         j = (d[57] & 0x0f);
         mixer[2][0] = i;
         mixer[2][1] = j;
         mixer[2][2] = d[58] > 127 ? d[58] - 256 : d[58];
         mixer[2][3] = d[59] > 127 ? d[59] - 256 : d[59];
         mixer[2][4] = d[60];
          
         switchA = d[61];
         switchB = d[62];
         
         variableA = d[63];
         variableB = d[64];
         valid = true;
      }
   }

