package com.zenoshrdlu.turborix;

import java.io.IOException;
import java.io.InputStream;

import com.zenoshrdlu.turborix.io.ChecksummedInputStream;

public class ChannelData {
    public int[] ch = new int[] {-1, -1, -1, -1, -1, -1, -1, -1};
    public boolean valid = false;

    public ChannelData() {}

    public static ChannelData readFrom(InputStream is) {
        @SuppressWarnings("resource")
        var in = new ChecksummedInputStream(is);
        var cd = new ChannelData();
        try {
            for (int i = 0; i < 7; i++) {
                cd.ch[i] = (in.read() & 0xFF) << 8 + (in.read() & 0xFF) - 1000;
            }
            cd.valid = in.validateChecksum();
        } catch (IOException e) {
            return null;
        }
        return cd;
    }
}
