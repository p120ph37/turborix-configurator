package com.zenoshrdlu.turborix.settings;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class CurvePoint {
    public int normal, idle;
    public void writeTo(OutputStream out) throws IOException {
        out.write(this.normal);
        out.write(this.idle);
    }
    public static CurvePoint readFrom(InputStream in) throws IOException {
        var cp = new CurvePoint();
        cp.normal = in.read() & 0xFF;
        cp.idle = in.read() & 0xFF;
        return cp;
    }
}
