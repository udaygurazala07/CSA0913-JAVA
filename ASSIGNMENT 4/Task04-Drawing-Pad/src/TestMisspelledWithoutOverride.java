package task04;

import java.awt.Frame;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class TestMisspelledWithoutOverride extends Frame {
    public TestMisspelledWithoutOverride() {
        addMouseListener(new MouseAdapter() {
            public void mouseclicked(MouseEvent e) {
                System.out.println("Clicked");
            }
        });
    }
}