package com.zenoshrdlu.turborix.settings;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class End {
    public int down, up;
    public void writeTo(OutputStream out) throws IOException {
        out.write(this.down);
        out.write(this.up);
    }
    public static End readFrom(InputStream in) throws IOException {
        var e = new End();
        e.down = in.read() & 0xFF;
        e.up = in.read() & 0xFF;
        return e;
    }
}
