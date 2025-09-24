package com.zenoshrdlu.turborix.settings;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class Dual {
    public int on, off;
    public void writeTo(OutputStream out) throws IOException {
        out.write(this.on);
        out.write(this.off);
    }
    public static Dual readFrom(InputStream in) throws IOException {
        var d = new Dual();
        d.on = in.read() & 0xFF;
        d.off = in.read() & 0xFF;
        return d;
    }
}
