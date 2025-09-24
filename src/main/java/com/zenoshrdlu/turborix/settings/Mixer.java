package com.zenoshrdlu.turborix.settings;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class Mixer {
    public int src, dst, up, dn, sw;
    public void writeTo(OutputStream out) throws IOException {
        out.write(this.src << 4 | this.dst);
        out.write(this.dst);
        out.write(this.up);
        out.write(this.dn);
        out.write(this.sw);
    }
    public static Mixer readFrom(InputStream in) throws IOException {
        var m = new Mixer();
        var srcDst = in.read();
        m.src = srcDst >> 4;
        m.dst = srcDst & 0x0f;
        m.up = in.read() & 0xFF;
        m.dn = in.read() & 0xFF;
        m.sw = in.read() & 0xFF;
        return m;
    }
}
