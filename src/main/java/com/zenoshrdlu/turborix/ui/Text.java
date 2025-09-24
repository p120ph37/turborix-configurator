package com.zenoshrdlu.turborix.ui;

import java.awt.Color;
import java.awt.TextField;

public class Text extends TextField {
    public Text() {
        this(4);
    }
    public Text(int n) {
        super("", n);
        setBackground(Color.cyan);
        setForeground(Color.black);
    }
}