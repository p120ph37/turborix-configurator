package com.zenoshrdlu.turborix.settings;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.BitSet;

import com.zenoshrdlu.turborix.io.ChecksummedInputStream;
import com.zenoshrdlu.turborix.io.ChecksummedOutputStream;

public class SettingData {

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
    public BitSet reverse = new BitSet(6);
    public Dual[] dual = new Dual[3];
    public int[] swash = new int[3];
    public End[] end = new End[6];
    public CurvePoint[] throttlecurve = new CurvePoint[5];
    public CurvePoint[] pitchcurve = new CurvePoint[5];
    public int[] subtrim = new int[6];
    public Mixer[] mixer = new Mixer[3];
    public int switchA;
    public int switchB;
    public int variableA;
    public int variableB;

    int chksum = 0;

    public SettingData() {}

    public void writeTo(OutputStream os) throws IOException {
        var out = new ChecksummedOutputStream(os);
        out.write(this.model << 4 | this.stick);
        out.write(this.reverse.toByteArray());
        for (int n = 0; n < 3; n++) {
            dual[n].writeTo(out);
        }
        for (int n = 0; n < 3; n++) {
            out.write(swash[n]); // signed byte
        }
        for (int n = 0; n < 6; n++) {
            end[n].writeTo(out);
        }
        for (int n = 0; n < 5; n++) {
            throttlecurve[n].writeTo(out);
        }
        for (int n = 0; n < 5; n++) {
            pitchcurve[n].writeTo(out);
        }
        for (int n = 0; n < 6; n++) {
            out.write(subtrim[n]); // signed byte
        }
        for (int n = 0; n < 3; n++) {
            mixer[n].writeTo(out);
        }
        out.write(this.switchA);
        out.write(this.switchB);
        out.write(this.variableA);
        out.write(this.variableB);
        out.writeChecksum();
    }

    public static SettingData readFrom(InputStream is) throws IOException {
        var sd = new SettingData();
        var in = new ChecksummedInputStream(is);
        int modelStick = in.read();
        sd.model = modelStick & 0x0f;
        sd.stick = modelStick & 0xf0 >> 4;
        sd.reverse = BitSet.valueOf(in.readNBytes(1));
        for (int n = 0; n < 3; n++) sd.dual[n] = Dual.readFrom(in);
        for (int n = 0; n < 3; n++) sd.swash[n] = in.read();
        for (int n = 0; n < 6; n++) sd.end[n] = End.readFrom(in);
        for (int n = 0; n < 5; n++) sd.throttlecurve[n] = CurvePoint.readFrom(in);
        for (int n = 0; n < 5; n++) sd.pitchcurve[n] = CurvePoint.readFrom(in);
        for (int n = 0; n < 6; n++) sd.subtrim[n] = in.read();
        for (int n = 0; n < 3; n++) sd.mixer[n] = Mixer.readFrom(in);
        sd.switchA = in.read();
        sd.switchB = in.read();
        sd.variableA = in.read();
        sd.variableB = in.read();
        sd.valid = in.validateChecksum();
        return sd;
    }
}