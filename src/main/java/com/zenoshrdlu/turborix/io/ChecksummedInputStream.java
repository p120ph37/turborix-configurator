package com.zenoshrdlu.turborix.io;

import java.io.IOException;
import java.io.InputStream;

public class ChecksummedInputStream extends InputStream {
      private int checksum = 0;
      private InputStream in;
      public ChecksummedInputStream(InputStream in) {
         this.in = in;
      }
      public int read() throws IOException {
         int b = this.in.read();
         this.checksum += b & 0xFF;
         return b;
      }
      public int getChecksum() {
         return this.checksum;
      }
      public boolean validateChecksum() {
         try {
            return this.checksum == 256 * (this.in.read() & 0xFF) + (this.in.read() & 0xFF);
         } catch (IOException e) {
            return false;
         }
      }
   }