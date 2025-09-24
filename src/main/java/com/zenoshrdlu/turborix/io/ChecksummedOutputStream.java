package com.zenoshrdlu.turborix.io;

import java.io.IOException;
import java.io.OutputStream;

public class ChecksummedOutputStream extends OutputStream {
      private int checksum = 0;
      private OutputStream out;
      public ChecksummedOutputStream(OutputStream out) {
         this.out = out;
      }
      public void write(int b) throws IOException {
         this.out.write(b);
         this.checksum += b & 0xFF;
      }
      public int getChecksum() {
         return this.checksum & 0xFFFF;
      }
      public void writeChecksum() throws IOException {
         this.out.write((this.checksum >> 8) & 0xFF);
         this.out.write(this.checksum & 0xFF);
      }
   }