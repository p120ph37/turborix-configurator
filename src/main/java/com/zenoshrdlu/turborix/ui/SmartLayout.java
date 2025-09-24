package com.zenoshrdlu.turborix.ui;

import java.awt.Component;
import java.util.Arrays;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import net.miginfocom.swing.MigLayout;

public class SmartLayout extends MigLayout {
    final private static String[] defaultLayoutConstraints = new String[] {
        "insets 8",
        "gap 4 4",
        "align center center"
    };
    final private static String[] defaultComponentConstraints = new String[] {
        "grow"
    };

    public SmartLayout(String ...layoutConstraints) {
        super(Stream.concat(
            Stream.of(defaultLayoutConstraints),
            Arrays.stream(layoutConstraints)
        ).collect(Collectors.joining(",")));
    }

    @Override
    public void addLayoutComponent(Component comp, Object constraints) {
        var s = Arrays.stream(defaultComponentConstraints);
        if (constraints instanceof String[] constraintStrings) {
            s = Stream.concat(s, Arrays.stream(constraintStrings));
        } else if (constraints instanceof String constraintString) {
            s = Stream.concat(s, Stream.of(constraintString));
        } else if (constraints == null) {
            // nothing to add
        } else {
            super.addLayoutComponent(comp, constraints); // not a string or string-array, so we can't add to it
            return;
        }
        super.addLayoutComponent(comp, s.collect(Collectors.joining(",")));
    }
}
