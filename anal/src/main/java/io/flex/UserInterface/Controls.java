package io.flex.UserInterface;

import javax.swing.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

public class Controls {
    private JSlider slider1;
    private JPanel panel1;

    public Controls() {
        slider1.addPropertyChangeListener(new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent propertyChangeEvent) {
            }
        });
    }
}
