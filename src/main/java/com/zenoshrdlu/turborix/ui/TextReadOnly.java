package com.zenoshrdlu.turborix.ui;

public class TextReadOnly extends Text {
    public TextReadOnly(int n) {
        super(n);
        setEditable(false);
    }
}